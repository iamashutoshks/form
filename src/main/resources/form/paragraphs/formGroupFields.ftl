[#assign cms=JspTaglibs["cms-taglib"]]

[#if model.requiredSymbol?has_content && model.rightText?has_content]
<p class="required"><span>${model.requiredSymbol?html}</span> ${model.rightText?html!""}</p>
[/#if]

[@cms.editBar editLabel="${i18n['form.fieldset.editLabel']}" /]
<fieldset>

    [#if content.title?has_content]
        <h2>${mgnl.encode(content).title!}</h2>
    [/#if]
    [#if content.fields?exists]
        [@cms.contentNodeIterator contentNodeCollectionName="fields"]
            [@cms.includeTemplate/]
        [/@cms.contentNodeIterator]
    [/#if]
</fieldset>

[@cms.newBar newLabel="${i18n['form.fieldset.fields.newLabel']}" contentNodeCollectionName="fields"  paragraph="${model.parent.paragraphsAsStringList}"/]







