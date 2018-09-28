package com.geely.mesh.demo.cartservice.controller;

import com.geely.mesh.demo.cartservice.domain.CommonResponse;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController("/")
public class CartController {
    private static final String RESPONSE_STRING_FORMAT = "customer => %s\n";
    private final RestTemplate userRestTemplate = new RestTemplate();
    private final RestTemplate orderRestTemplate = new RestTemplate();

    private static Set<Long> verfiedUsers = Sets.newConcurrentHashSet();

    @Value("${userservice.api.url:http://userservice:8888}")
    private String userServiceURL;

    @Value("${orderservice.api.url:http://userservice:8488}")
    private String orderServiceURL;

    @ApiOperation(value="获取用户订单列表", notes="根据url的userid来获取用户订单列表")
    @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/myorders/{userid}", method= RequestMethod.GET)
    public ResponseEntity<String> getMyOrders(@PathVariable Long userid) {
        if(!verfiedUsers.contains(userid)) {
            return new ResponseEntity<>("Please login first！", HttpStatus.FORBIDDEN);
        }
        String getMyOrdersUrl = orderServiceURL + "/orders/{1}";
        ResponseEntity<CommonResponse> myOrdersResponse = orderRestTemplate.getForEntity(getMyOrdersUrl, CommonResponse.class, userid);
        if(myOrdersResponse.getBody().getCode() == 0) {
            return ResponseEntity.ok(myOrdersResponse.getBody().getResult());
        }
        else {
            return ResponseEntity.ok("Fetch my orderlist failed: " + myOrdersResponse.getBody().getCause());
        }
    }

    @RequestMapping(value="/addtocart/{userid}", method=RequestMethod.POST)
    @ApiOperation(value="购物车下订单", notes="根据传入的下单信息来创建订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "orderParams", value = "下单信息", required = true, dataType = "Map<String, Object>")
    })
    public ResponseEntity<String> makeOrder(@PathVariable Long userid, @RequestBody Map<String, Object> orderParams) {
        if(!verfiedUsers.contains(userid)) {
            return new ResponseEntity<>("Please login first！", HttpStatus.FORBIDDEN);
        }
        String payUrl = orderServiceURL + "/orders/";
        HttpHeaders orderHeaders = new HttpHeaders();
        orderHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        Map<String, Object> params = new HashMap<>(orderParams);
        params.put("userId", userid);
        HttpEntity<String> orderEntity = new HttpEntity<String>(JSONObject.fromObject(params).toString(), orderHeaders);
        return userRestTemplate.postForEntity(payUrl, orderEntity, String.class);
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    @ApiOperation(value="用户校验登录", notes="根据用户名和密码登录获取用户ID")
    @ApiImplicitParam(name = "loginParams", value = "用户登录信息", required = true, dataType = "Map<String, String>")
    public ResponseEntity<String> userLogin(@RequestBody String loginParams) {
        try {
            String userLoginUrl = userServiceURL + "/users/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

            HttpEntity<String> requestEntity = new HttpEntity<String>(loginParams, headers);
            ResponseEntity<CommonResponse> responseEntity = userRestTemplate.postForEntity(userLoginUrl, requestEntity, CommonResponse.class);
            if(responseEntity.getBody().getCode() == 0)
            {
                String result = responseEntity.getBody().getResult();
                JSONObject jsonObj = JSONObject.fromObject(result);

                long userId = jsonObj.getLong("userId");
                String getUserInfoUrl = userServiceURL + "/users/{1}";
                ResponseEntity<CommonResponse> userinfoResponse = userRestTemplate.getForEntity(getUserInfoUrl, CommonResponse.class, userId);
                JSONObject userinfo = JSONObject.fromObject(userinfoResponse.getBody().getResult());
                String userName = userinfo.getString("name");
                Integer userAge = userinfo.getInt("age");
                Map<String, Object> userDetail = Maps.newHashMap();
                userDetail.put("name", userName);
                userDetail.put("userId", userId);
                userDetail.put("age", userAge);
                verfiedUsers.add(userId);
                return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, JSONObject.fromObject(userDetail).toString()));
            }
            else {
                return ResponseEntity.ok(responseEntity.getBody().getCause());
            }
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT,
                            String.format("%d %s", ex.getRawStatusCode(), createHttpErrorResponseString(ex))));
        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getMessage()));
        }
    }
    private String createHttpErrorResponseString(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString().trim();
        if (responseBody.startsWith("null")) {
            return ex.getStatusCode().getReasonPhrase();
        }
        return responseBody;
    }
}

