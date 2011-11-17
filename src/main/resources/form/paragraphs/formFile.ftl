[@cms.edit /]
<div ${model.style!}>
    [#if content.title?has_content]
        <label for="${content.controlName}">
            ${content.title!}
      </label>
    [/#if]
        <input type="file" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}"/>

    [#if content.description?has_content]
      <span>${content.description}</span>
    [/#if]

</div>
