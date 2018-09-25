package com.geely.mesh.demo.cartservice.controller;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController("/")
public class CartController {
    private static final String RESPONSE_STRING_FORMAT = "customer => %s\n";
    private final RestTemplate restTemplate;

    @Value("${userservice.api.url:http://userservice:8888}")
    private String userserviceURL;

    public CartController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    @ApiOperation(value="用户校验登录", notes="根据用户名和密码登录获取用户ID")
    @ApiImplicitParam(name = "loginParams", value = "用户登录信息", required = true, dataType = "Map<String, String>")
    public ResponseEntity<String> getCustomer(@RequestBody String loginParams) {
        try {
            String userLoginUrl = userserviceURL + "/users/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

            HttpEntity<String> requestEntity = new HttpEntity<String>(loginParams, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(userLoginUrl, requestEntity, String.class);
            if(responseEntity.getStatusCode() == HttpStatus.OK)
            {
                String response = responseEntity.getBody();
                JSONObject jsonObj = JSONObject.fromObject(response);
                if(!jsonObj.containsKey("code")) {
                    long userId = jsonObj.getLong("userId");
                    String getUserInfoUrl = userserviceURL + "/users/{1}";
                    ResponseEntity<String> userinfoResponse = restTemplate.getForEntity(getUserInfoUrl, String.class, userId);
                    JSONObject userinfo = JSONObject.fromObject(userinfoResponse.getBody());
                    String userName = userinfo.getString("name");
                    Integer userAge = userinfo.getInt("age");
                    Map<String, Object> userDetail = Maps.newHashMap();
                    userDetail.put("name", userName);
                    userDetail.put("userId", userId);
                    userDetail.put("age", userAge);

                    return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, JSONObject.fromObject(userDetail).toString()));
                }
                else
                {
                    String cause = jsonObj.getString("cause");
                    return ResponseEntity.ok(cause);
                }
            }
            else {
                return responseEntity;
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

