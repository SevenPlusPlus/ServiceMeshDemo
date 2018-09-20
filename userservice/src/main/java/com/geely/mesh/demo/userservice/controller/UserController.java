package com.geely.mesh.demo.userservice.controller;

import com.geely.mesh.demo.userservice.domain.User;
import com.geely.mesh.demo.userservice.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value="获取用户列表", notes="")
    @RequestMapping(value="/", method=RequestMethod.GET)
    public List<User> getUserList() {
        return userService.getAllUsers();
    }

    @ApiOperation(value="创建用户", notes="根据User对象创建用户")
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String postUser(@RequestBody User user) {
        Long userId = userService.saveUser(user);
        return "success: [userid:" + userId + "]";
    }

    @ApiOperation(value="获取用户详细信息", notes="根据url的id来获取用户详细信息")
    @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/{userid}", method=RequestMethod.GET)
    public User getUser(@PathVariable Long userid) {
        return userService.getUserById(userid);
    }

    @ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    })
    @RequestMapping(value="/{userid}", method=RequestMethod.PUT)
    public String putUser(@PathVariable Long userid, @RequestBody User user) {
        user.setUserId(userid);
        userService.updateUser(user);
        return "success";
    }

    @ApiOperation(value="删除用户", notes="根据url的id来指定删除对象")
    @ApiImplicitParam(name = "userid", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value="/{userid}", method=RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long userid) {
        boolean bret = userService.deleteUserById(userid);
        if(bret) {
            return "success";
        }
        else {
            return "failed";
        }
    }
}
