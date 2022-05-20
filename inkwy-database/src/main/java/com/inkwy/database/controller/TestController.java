package com.inkwy.database.controller;

import cn.hutool.core.io.FileUtil;
import com.inkwy.database.domain.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.*;
import java.util.Base64;
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

    @GetMapping("/netSourceToBase64")
    @ApiOperation("netSourceToBase64")
    public String netSourceToBase64(@RequestParam String srcUrl) {
        ByteArrayOutputStream outPut = new ByteArrayOutputStream();
        byte[] data = new byte[1024 * 8];
        try {
            // 创建URL
            URL url = new URL(srcUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //连接失败/链接失效/文件不存在
                return null;
            }
            InputStream inStream = conn.getInputStream();
            int len = -1;
            while (-1 != (len = inStream.read(data))) {
                outPut.write(data, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outPut.toByteArray()).replace("\r","").replace("\n","");
    }

    @PostMapping("/pdfToBase64")
    @ApiOperation("pdf转Base64")
    public String  pdfToBase64(MultipartFile multipartFile) {
        Base64.Encoder encoder = Base64.getEncoder();
        FileInputStream fin =null;
        BufferedInputStream bin =null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout =null;
        File file = null;
        try {
            file = transferToFile(multipartFile);
            fin = new FileInputStream(file);
            bin = new BufferedInputStream(fin);
            baos = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while(len != -1){
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节
            bout.flush();
            byte[] bytes = baos.toByteArray();
            log.info("--------------------------");
            System.out.println(encoder.encodeToString(bytes));
            return encoder.encodeToString(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fin.close();
                bin.close();
                bout.close();
                FileUtil.del(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public File transferToFile(MultipartFile multipartFile) {
        //选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            file = new File("D:\\usr\\test.pdf");
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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
