package edu.kust.facedeepseek.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("image")
public class VideoController {


    @PostMapping("/registerimage")
    @ResponseBody
    public String  getImageBase64(@RequestBody String image){

        //接到前端传的base64参数
        System.out.println(image);

        return "ok";
    }



}
