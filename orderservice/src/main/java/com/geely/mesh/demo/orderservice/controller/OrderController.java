package com.geely.mesh.demo.orderservice.controller;

import com.geely.mesh.demo.orderservice.domain.Order;
import com.geely.mesh.demo.orderservice.monitor.PrometheusMetrics;
import com.geely.mesh.demo.orderservice.service.OrderService;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final RestTemplate userRestTemplate = new RestTemplate();
    private final RestTemplate inventoryRestTemplate = new RestTemplate();

    @Value("${userservice.api.url:http://userservice:8888}")
    private String userServiceURL;
    @Value("${inventoryservice.api.url:http://inventoryservice:8388}")
    private String inventoryServiceURL;

    @Autowired
    private OrderService orderService;

    @PrometheusMetrics
    @ApiOperation(value="获取用户订单列表", notes="根据url的userid来获取用户订单列表")
    @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/{userid}", method= RequestMethod.GET)
    public ResponseEntity<List<Order>> getMyOrders(@PathVariable Long userid) {

        List<Order> myorders = orderService.getOrdersByUserId(userid);
        return new ResponseEntity<>(myorders, HttpStatus.OK);
    }

    @PrometheusMetrics
    @ApiOperation(value="删除订单", notes="根据url的orderid来指定删除订单")
    @ApiImplicitParam(name = "orderid", value = "订单ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/{orderid}", method=RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long orderid) {
        boolean bret = orderService.deleteOrderByOrderId(orderid);
        if(bret) {
            return "success";
        }
        else {
            throw new RuntimeException("Delete order failed");
        }
    }

    @PrometheusMetrics
    @RequestMapping(value="/", method=RequestMethod.POST)
    @ApiOperation(value="用户下订单", notes="根据传入的下单信息来创建订单")
    @ApiImplicitParam(name="orderParams", value="下单信息", required = true, dataType = "Map<String, Object>")
    public ResponseEntity<String> makeOrder(@RequestBody Map<String, Object> orderParams) {
        Long userId = Long.parseLong(orderParams.get("userId").toString());
        Long productId = Long.parseLong(orderParams.get("productId").toString());
        Long count = Long.parseLong(orderParams.get("count").toString());

        String calcUrl = inventoryServiceURL + "/calc";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(calcUrl)
                .queryParam("productid", productId)
                .queryParam("count", count);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        HttpEntity<String> response = inventoryRestTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
        Long amount = Long.parseLong(response.getBody());

        Order newOrder = orderService.createOrder(userId, productId, count, amount);

        String payUrl = userServiceURL + "/{1}/pay";
        HttpHeaders payHeaders = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        Map<String, Object> payParams = Maps.newHashMap();
        payParams.put("amount", amount);
        HttpEntity<String> payRequestEntity = new HttpEntity<String>(JSONObject.fromObject(payParams).toString(), payHeaders);
        ResponseEntity<String> payResult = userRestTemplate.postForEntity(payUrl, payRequestEntity, String.class, userId);
        JSONObject jsonObj = JSONObject.fromObject(payResult.getBody());
        if(jsonObj.containsKey("code"))
        {
            orderService.changeOrderStatus(newOrder.getOrderId(), 2);
            return ResponseEntity.ok("pay order failed: " + payResult.getBody());
        }
        orderService.changeOrderStatus(newOrder.getOrderId(), 1);

        String stockOutUrl = inventoryServiceURL + "/{1}/out";
        HttpHeaders stockoutHeaders = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        Map<String, Object> stockoutParams = Maps.newHashMap();
        payParams.put("count", count);
        HttpEntity<String> stockoutRequestEntity = new HttpEntity<String>(JSONObject.fromObject(stockoutParams).toString(), stockoutHeaders);
        ResponseEntity<String> stockoutResult = userRestTemplate.postForEntity(stockOutUrl, stockoutRequestEntity, String.class, productId);
        JSONObject stockoutJsonObj = JSONObject.fromObject(stockoutResult.getBody());
        if(stockoutJsonObj.containsKey("code"))
        {
            orderService.changeOrderStatus(newOrder.getOrderId(), 4);
            return ResponseEntity.ok("Order inventory stockout failed: " + stockoutResult.getBody());
        }
        orderService.changeOrderStatus(newOrder.getOrderId(), 3);

        return ResponseEntity.ok("Make order successfully");
    }
}
