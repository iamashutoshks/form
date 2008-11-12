[#assign cms=JspTaglibs["cms-taglib"]]

[#if mgnl.editMode]
    [@cms.editBar /]
[/#if]

[#assign parent = content?parent]
[#if content.rightText?has_content]
    <p class="required"><span>${parent.requiredSymbol?default("*")}</span> ${content.rightText}</p>
[/#if]
<fieldset>

    [#if content.title?has_content]
        <h2>${content.title}</h2>
    [/#if]
    [#if content.fields?exists]
        [@cms.contentNodeIterator contentNodeCollectionName="fields"]
            [@cms.includeTemplate/]
        [/@cms.contentNodeIterator]
    [/#if]
</fieldset>


[#if mgnl.editMode]

[@cms.newBar contentNodeCollectionName="fields"  paragraph="formEdit,formGroupEdit,formFile,formGroupSelect,formRadioCheck,formSubmit"/]

[/#if]





