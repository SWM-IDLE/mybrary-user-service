package kr.mybrary.userservice.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheKey {

    INTEREST_BASED_BOOK_RECOMMENDATION("bookRecommendation", "interestBased", CacheTTL.ONE_WEEK.getExpireTimeSeconds());

    private final String prefix;
    private final String key;
    private final int expireTimeSeconds;

}
