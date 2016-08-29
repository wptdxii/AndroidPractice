package com.wptdxii.androidpractice.network.retrofit.rx.func;

import com.wptdxii.androidpractice.model.BaseModel;
import com.wptdxii.androidpractice.util.json.JsonConvert;

import rx.functions.Func1;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 将String转换为BaseModel<T>
 *  RxJava 中的 flatMap()
 */
public class BaseModelFunc<T> implements Func1<String, BaseModel<T>> {
    @Override
    public BaseModel<T> call(String result) {
        JsonConvert<BaseModel<T>> convert = new JsonConvert<>();
        return convert.parse(result);
    }
}