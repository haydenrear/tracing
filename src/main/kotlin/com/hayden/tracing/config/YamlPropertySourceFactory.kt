package com.hayden.tracing.config

import com.hayden.utilitymodule.nullable.mapNullable
import com.hayden.utilitymodule.nullable.or
import com.hayden.utilitymodule.nullable.orElseGet
import jakarta.annotation.Nullable
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory

open class YamlPropertySourceFactory : PropertySourceFactory {
    override fun createPropertySource(@Nullable name: String?, encodedResource: EncodedResource): PropertySource<*> =
        name.mapNullable {
                PropertiesPropertySource(
                    it,
                    YamlPropertiesFactoryBean().also { it.setResources(encodedResource.resource) }.getObject()!!
                )
            }
            .orElseGet {
                PropertiesPropertySource(
                    "application.yml",
                    YamlPropertiesFactoryBean().also { it.setResources(encodedResource.resource) }.getObject()!!
                )
            }
}