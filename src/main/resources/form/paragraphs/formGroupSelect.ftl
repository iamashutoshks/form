[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.editBar /]
    [#if mgnl.editMode]
    <p>${i18n['form.groupselect.note']}</p><br />
    [/#if]
    [#if content.title?has_content]
        <label id="${content.controlName}_label" for="${content.controlName}">
            ${content.title}
    [/#if]
    [#if !mgnl.editMode]
        <select id="${content.controlName}" name="${content.controlName}" ${content.multiple?string("multiple=\"multiple\"", "")} >
    [/#if]

            [#if content.groups?exists]
                [@cms.contentNodeIterator contentNodeCollectionName="groups"]
                    [@cms.includeTemplate/]
                [/@cms.contentNodeIterator]
            [/#if]
    [#if !mgnl.editMode]
        </select>
    [/#if]
    [#if content.title?has_content]
        </label>
    [/#if]
    <div>
        [@cms.newBar contentNodeCollectionName="groups" paragraph="formGroupSelectGroups" /]
    </div>
</div>
