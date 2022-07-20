package vn.edu.fpt.rebroland.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    public static final Integer EXPIRE_MINUTES = 1;

    private LoadingCache<String, Integer> otpCache;

    public OtpService() {
        super();
        otpCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key){
                        return null;
                    }
                });
    }

    public int generateOtp(String key){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        otpCache.put(key, otp);
        return otp;
    }

    public Integer getOtp(String key){
        try {
            return otpCache.get(key);
        }catch (Exception e){
            return null;
        }
    }

    public void clearOtp(String key){
        otpCache.invalidate(key);
    }


}
