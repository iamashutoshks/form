
[#if model.requiredSymbol?has_content && model.rightText?has_content]
<p class="required"><span>${model.requiredSymbol}</span> ${model.rightText!""}</p>
[/#if]

[@cms.edit /]
<fieldset>

    [#if content.title?has_content]
        <h2>${cmsfn.encode(cmsfn.asJCRNode(content)).title!}</h2>
    [/#if]
    [@cms.area name="fields"/]
</fieldset>
