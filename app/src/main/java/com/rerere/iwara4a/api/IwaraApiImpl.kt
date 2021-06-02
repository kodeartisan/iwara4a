package com.rerere.iwara4a.api

import com.rerere.iwara4a.api.service.IwaraParser
import com.rerere.iwara4a.api.service.IwaraService

/**
 * IwaraAPI接口的具体实现
 *
 * 内部持有 iwaraParser 和 iwaraService 两个模块, 根据资源是否可以
 * 通过restful api直接访问来选择使用哪个模块获取数据
 */
class IwaraApiImpl(
    private val iwaraParser: IwaraParser,
    private val iwaraService: IwaraService
): IwaraApi {

}