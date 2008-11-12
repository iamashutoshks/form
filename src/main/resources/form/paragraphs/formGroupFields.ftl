[#assign cms=JspTaglibs["cms-taglib"]]


[@cms.editBar /]

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

[@cms.newBar contentNodeCollectionName="fields"  paragraph="${model.parentModel.paragraphsAsStringList}"/]







