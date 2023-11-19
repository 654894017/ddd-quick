package com.damon.demo.infrastructure.gateway_impl.points;

import com.damon.demo.domain.gateway.point.IPointGateway;
import okhttp3.*;

public class PointGateway implements IPointGateway {
    @Override
    public void tryDeductionPoints(Long bizId, Long points, Long orderSubmitUserId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"bizId\": 59,\n    \"deductedPoints\": 39\n}");
        Request request = new Request.Builder()
                .url("http://localhost:9898/v0.1/points_account/1/try_deduction_points")
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "localhost:9898")
                .addHeader("Connection", "keep-alive")
                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.code());
//        System.out.println(response.body().string());
    }

    @Override
    public void commitDeductionPoints(Long bizId) {

    }

    @Override
    public void cancelDeductionPoints(Long bizId) {

    }

    @Override
    public Long calculateDeductionMoney(Long deductionPoints, Long orderSubmitUserId) {
        return null;
    }
}
