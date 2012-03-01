

[#if model.requiredSymbol?has_content && model.rightText?has_content]
    <p class="required"><span>${model.requiredSymbol}</span> ${model.rightText}</p>
[/#if]

<fieldset>
    [#assign title = content.title!cmsfn.editMode?string("Empty Field Set", "")]
    [#if title?has_content]
        <h2>${title}</h2>
    [/#if]

    [@cms.area name="fields"/]
</fieldset>
