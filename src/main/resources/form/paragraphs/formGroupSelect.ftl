[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.editBar /]
    [#if content.title?has_content]
        <label id="${content.controlName}_label" for="${content.controlName}">
            ${content.title}
    [/#if]
        <select id="${content.controlName}" name="${content.controlName}" ${content.multiple?string("multiple=\"multiple\"", "")} >
            [#if content.groups?exists]
                [@cms.contentNodeIterator contentNodeCollectionName="groups"]
                    [@cms.includeTemplate/]
                [/@cms.contentNodeIterator]
            [/#if]
        </select>
    [#if content.title?has_content]
        </label>
    [/#if]
    <div>
        [@cms.newBar contentNodeCollectionName="groups" paragraph="formGroupSelectGroups" /]
    </div>
</div>
