package com.atguigu.bloggateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JwtUtil {

    // 密钥（需严格保密）
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("sk-84a8a0716afe4b379998dbab676be3e5sk-84a8a0716afe4b379998dbab676be3e5".getBytes(StandardCharsets.UTF_8));

    // 过期时间（示例：24小时）
    private static final long EXPIRE = TimeUnit.HOURS.toMillis(24);

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    public static List<String> getUserAuthorityFromToken(String token)
    {
        try
        {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("claim auth: {}", claims.get("authorities"));
            List<String> authorityList = (List<String>) claims.get("authorities");
            return authorityList;
        } catch (Exception e)
        {
            log.error("获取权限信息失败: {}", e.getMessage());
            return null;
        }
    }
}
