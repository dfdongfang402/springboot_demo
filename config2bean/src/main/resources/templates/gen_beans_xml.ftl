<?xml version="1.0" encoding="utf-8"?>
<namespace name="${fname}">
<#list beans as bn>
    <bean name="${bn.name}" from="${bn.fromXls[0]}" genxml="${bn.genxml}" <#if bn.baseclass! !="" > baseclass="${bn.baseclass}" </#if> >
    <#list bn.variables as myvar>
    <#if myvar.valueType! !="">
        <variable name="${myvar.name}" type="${myvar.type}" value="${myvar.valueType}" fromCol="${myvar.fromCol}" />
    <#else>
        <variable name="${myvar.name}" type="${myvar.type}" fromCol="${myvar.fromCol}" />
    </#if>
    </#list>
    </bean>
</#list>

</namespace>