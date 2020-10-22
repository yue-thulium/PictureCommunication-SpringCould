package com.picture.identity_service.entity.result;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;

/**
 * Created on 2020/9/28
 *
 * @author Yue Wu
 * @since 2020/9/28
 */
@Component
@RequestScope
public class ResultMod extends HashMap<String, Object> {

    public final Integer SUCCESS_CODE = 200;
    public final Integer FAIL_CODE = 400;

    public ResultMod message(String message) {
        this.put("message", message);
        return this;
    }

    public ResultMod success() {
        this.put("result", "success");
        this.code(SUCCESS_CODE);
        return this;
    }

    public ResultMod fail() {
        this.put("result", "fail");
        this.code(FAIL_CODE);
        return this;
    }

    public ResultMod put(String key,Object value) {
        super.put(key, value);
        return this;
    }

    public ResultMod code(Integer code) {
        this.put("code", code);
        return this;
    }

}
