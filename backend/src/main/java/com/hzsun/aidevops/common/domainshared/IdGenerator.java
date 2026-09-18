package com.hzsun.aidevops.common.domainshared;

/**
 * 系统内部 ID 生成器。
 *
 * <p>领域层只依赖本接口，不得直接依赖 Hutool、Spring、数据库、配置中心或 Mapper。
 * 具体实现（雪花算法、workerId 分配）位于基础设施与启动装配层。</p>
 */
public interface IdGenerator {

    /**
     * 生成下一个系统内部 ID。
     *
     * @return 雪花 ID
     */
    Long nextId();
}
