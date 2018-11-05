package com.bytegen.common.web;

import com.bytegen.common.web.filter.Authentication;
import com.bytegen.common.web.util.ResponseUtil;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by xiang.gao on 2018/5/15.
 * Description:
 */
@Controller
@RequestMapping("")
public class TestController {

    @Authentication
    @GetMapping("test/1/success")
    public ResponseEntity success(@RequestParam("para") String para,
                                  @RequestParam(value = "para_name", required = false) String paraName) throws Exception {

        Map<String, String> data = Maps.newHashMap();
        data.put("para", para);
        data.put("para_name", paraName);

        return ResponseUtil.toSuccessResponse(data);
    }

    @GetMapping("test/1/fail")
    public ResponseEntity fail(@RequestParam("para") String para,
                               @RequestParam(value = "para_name", required = false) String paraName) throws Exception {

        return ResponseUtil.toBaseResponse(RSEnum.RS_FAILURE.getCode());
    }

    @PostMapping(value = "test/1/post")
    public ResponseEntity post(@RequestParam("body_para") String para,
                               @RequestParam(value = "para_name", required = false) String paraName) throws Exception {
        Map<String, String> data = Maps.newHashMap();
        data.put("body_para", para);
        data.put("para_name", paraName);

        return ResponseUtil.toSuccessResponse(data);
    }

    @PostMapping(value = "test/1/json")
    public ResponseEntity postJson(@RequestBody JsonObject payload) throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("payload", payload);

        return ResponseUtil.toSuccessResponse(data);
    }

    @PutMapping(value = "test/1/json")
    public ResponseEntity putJson(@RequestBody JsonObject payload) throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("payload", payload);

        return ResponseUtil.toSuccessResponse(data);
    }

}
