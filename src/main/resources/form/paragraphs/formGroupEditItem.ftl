[#assign cms=JspTaglibs["cms-taglib"]]

[#if mgnl.editMode]
        <div style="float:right;height:20px;width:110px">[@cms.editBar /]</div>
[/#if]
<div ${model.style!} >

[#if content.title?has_content]
    <label for="${content.controlName}">
        <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${mgnl.encode(content).title!}
            [#if content.mandatory]
                 <dfn title="required">${model.requiredSymbol!}</dfn>
            [/#if]
        </span>
   </label>
[/#if]


<input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}" maxlength="${content.maxLength!'50'}"/>

</div>
