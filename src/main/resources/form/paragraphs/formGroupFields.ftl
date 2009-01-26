[#assign cms=JspTaglibs["cms-taglib"]]

[#if content.displayRequiredSymbol!false]
<p class="required"><span>${model.requiredSymbol!""}</span> ${model.rightText!""}</p>
[/#if]

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

[@cms.newBar newLabel="New Field" contentNodeCollectionName="fields"  paragraph="${model.parent.paragraphsAsStringList}"/]







