[#assign cms=JspTaglibs["cms-taglib"]]


[#if content.title?has_content]
    <label ${model.style!} id="${content.controlName}_label" for="${content.controlName}">
        <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${content.title}
            [#if content.mandatory]
                 <dfn title="required">*</dfn>
            [/#if]
        </span>
[/#if]

[#if mgnl.editMode]
        <div style="float:right;height:20px;width:100px">[@cms.editBar /]</div>
[/#if]

<input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!}"/>

[#if content.title?has_content]
    </label>
[/#if]

