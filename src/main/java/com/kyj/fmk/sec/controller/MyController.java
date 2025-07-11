package com.kyj.fmk.sec.controller;

import com.kyj.fmk.sec.annotation.PrivateEndpoint;
import com.kyj.fmk.sec.annotation.PublicEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/my")
    @ResponseBody
    @PublicEndpoint
    public String myApi(){

        return "my route";
    }
}
