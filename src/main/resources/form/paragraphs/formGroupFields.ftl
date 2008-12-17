[#assign cms=JspTaglibs["cms-taglib"]]


[#assign parent = content?parent]

<p class="required"><span>${parent.requiredSymbol?default("*")}</span> ${parent.rightText!""}</p>

[@cms.editBar editLabel="Edit Fieldset" /]
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

[@cms.newBar contentNodeCollectionName="fields"  paragraph="${model.parent.paragraphsAsStringList}"/]







