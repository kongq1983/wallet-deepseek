package com.hzsun.aidevops.common.infrastructure.id;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.lang.Snowflake;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import org.springframework.stereotype.Component;

/**
 * 基于 Hutool 雪花算法的内部 ID 生成器。
 *
 * <p>datacenterId 固定为 1，workerId 在启动时从数据库单行计数器分配。
 * 时钟回拨异常由 Hutool 抛出，必须视为基础设施故障，不得在业务层吞掉或降级为自增/随机 ID。</p>
 */
@Component
public class SnowflakeIdGenerator implements IdGenerator {

    /** 数据中心标识，按规范固定为 1。 */
    private static final long DATACENTER_ID = 1L;

    private final Snowflake snowflake;

    public SnowflakeIdGenerator(DatabaseWorkerIdAllocator workerIdAllocator) {
        this.snowflake = IdUtil.getSnowflake(workerIdAllocator.allocate(), DATACENTER_ID);
    }

    @Override
    public Long nextId() {
        return snowflake.nextId();
    }
}
