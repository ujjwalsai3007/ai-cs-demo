package org.example.aicsdemo.controller

import org.example.aicsdemo.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClient

@Controller
@RequestMapping("/system")
class SystemInfoController(
    private val productService: ProductService,
    private val restClient: RestClient,
) {
    @GetMapping("/info")
    fun getSystemInfo(model: Model): String {
        val runtimeVersion = System.getProperty("java.version")
        val vmName = System.getProperty("java.vm.name")
        val vmVendor = System.getProperty("java.vm.vendor")
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val osArch = System.getProperty("os.arch")
        val threadsVirtual = Thread.currentThread().isVirtual

        // Count active virtual threads
        val virtualThreadCount = Thread.getAllStackTraces().keys.count { it.isVirtual }
        val platformThreadCount = Thread.getAllStackTraces().keys.count { !it.isVirtual }

        model.addAttribute("javaVersion", runtimeVersion)
        model.addAttribute("vmName", vmName)
        model.addAttribute("vmVendor", vmVendor)
        model.addAttribute("osName", osName)
        model.addAttribute("osVersion", osVersion)
        model.addAttribute("osArch", osArch)
        model.addAttribute("threadsVirtual", threadsVirtual)
        model.addAttribute("virtualThreadCount", virtualThreadCount)
        model.addAttribute("platformThreadCount", platformThreadCount)
        model.addAttribute(
            "springVersion",
            org.springframework.core.SpringVersion
                .getVersion(),
        )
        model.addAttribute("kotlinVersion", KotlinVersion.CURRENT.toString())
        model.addAttribute("restClientClass", restClient.javaClass.name)
        model.addAttribute("jdkHttpClientUsed", restClient.javaClass.name.contains("Jdk"))
        model.addAttribute("loomEnabled", System.getProperty("spring.threads.virtual.enabled", "false"))

        return "system-info"
    }
}
