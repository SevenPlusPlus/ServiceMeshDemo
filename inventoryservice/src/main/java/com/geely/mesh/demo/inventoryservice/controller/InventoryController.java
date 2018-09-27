package com.geely.mesh.demo.inventoryservice.controller;

import com.geely.mesh.demo.inventoryservice.domain.Inventory;
import com.geely.mesh.demo.inventoryservice.monitor.PrometheusMetrics;
import com.geely.mesh.demo.inventoryservice.service.InventoryService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PrometheusMetrics
    @ApiOperation(value="获取商品库存信息", notes="根据url的productId来获取商品库存信息")
    @ApiImplicitParam(name = "productid", value = "商品ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/{productid}", method= RequestMethod.GET)
    public ResponseEntity<Inventory> getProduct(@PathVariable Long productid) {

        Inventory inventory = inventoryService.getProductById(productid);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

    @PrometheusMetrics
    @ApiOperation(value="计算商品总价", notes = "根据查询参数productid和count计算商品总价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productid", value = "商品ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "商品数量", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value="/calc", method = RequestMethod.GET)
    public ResponseEntity<Long> calcTotalAmount(@RequestParam("productid") Long productid,
                                                @RequestParam("count") Long count) {
        Long total = inventoryService.calcProductTotalFee(productid, count);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @PrometheusMetrics
    @ApiOperation(value="获取商品库存列表", notes="")
    @RequestMapping(value="/", method=RequestMethod.GET)
    public List<Inventory> getUserList() {
        return inventoryService.getAllProducts();
    }

    @PrometheusMetrics
    @ApiOperation(value="创建商品库存", notes="根据Inventory对象创建商品库存")
    @ApiImplicitParam(name = "inventory", value = "商品库存实体inventory", required = true, dataType = "Inventory")
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String postInventory(@RequestBody Inventory inventory) {
        Long productId = inventoryService.createProduct(inventory);
        return "success: [productId:" + productId + "]";
    }

    @RequestMapping(value="/{productid}/in", method=RequestMethod.POST)
    @ApiOperation(value="新库存入库", notes="根据url的id来指定更新对象，并根据传过来的入库信息来更新商品库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productid", value = "商品ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "kvParams", value = "商品入库信息", required = true, dataType = "Map<String, Object>")
    })
    public ResponseEntity<Long> stockIn(@PathVariable Long productid, @RequestBody Map<String, Object> kvParams) {
        Long count = Long.parseLong(kvParams.get("count").toString());
        Long newAvail = inventoryService.incInventory(productid, count);
        return new ResponseEntity<>(newAvail, HttpStatus.OK);
    }

    @RequestMapping(value="/{productid}/out", method=RequestMethod.POST)
    @ApiOperation(value="商品出库", notes="根据url的id来指定更新对象，并根据传过来的出库信息来更新商品库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productid", value = "商品ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "kvParams", value = "商品出库信息", required = true, dataType = "Map<String, Object>")
    })
    public ResponseEntity<Long> stockOut(@PathVariable Long productid, @RequestBody Map<String, Object> kvParams) {
        Long count = Long.parseLong(kvParams.get("count").toString());
        Long newAvail = inventoryService.decInventory(productid, count);
        return new ResponseEntity<>(newAvail, HttpStatus.OK);
    }
}
