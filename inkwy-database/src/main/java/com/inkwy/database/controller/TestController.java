package com.inkwy.database.controller;

import com.inkwy.database.domain.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @description:
 * @author: pjp
 * @date: 2022/2/28 11:37
 */

@RestController
@Api(tags = "测试", value = "TestController")
@RequestMapping(value = "/test")
@Slf4j
public class TestController {

    @ApiOperation(value = "获取ip")
    @PostMapping("/getIp")
    public Response getIp() {
        return Response.success(getInetAddresses());
    }

    @GetMapping("/init")
    @ApiOperation("接口测试")
    public Response init(){
        return Response.respSuccess("success");
    }

    public static String getInetAddresses() {
        Enumeration<NetworkInterface> nis;
        String ip = null;
        boolean flag = false;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
            for (; nis.hasMoreElements();) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                for (; ias.hasMoreElements();) {
                    InetAddress ia = ias.nextElement();
                    if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
                        ip = ia.getHostAddress();
                        flag = true;
                        break;
                    }
                }
                if (flag){
                    break;
                }
            }
        } catch (SocketException e) {
        }
        return ip;
    }
}
