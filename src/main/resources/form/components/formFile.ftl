

<div ${model.style!}>
    [#if content.title?has_content]
        <label for="${content.controlName!}">
            <span>${content.title}</span>
        </label>
    [/#if]

    <input type="file" name="${content.controlName!}" id="${content.controlName!}" value="${model.value}"/>

    [#if content.description?has_content]
        <span>${content.description}</span>
    [/#if]
</div><!-- end ${model.style!} -->
