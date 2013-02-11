[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!}>
[@cms.editBar /]
    [#if content.title?has_content]
        <label for="${content.controlName?html}">
            ${mgnl.encode(content).title!}
    	</label>
    [/#if]
        <input type="file" name="${content.controlName?html}" id="${content.controlName?html}" value="${model.value!?html}"/>

    [#if content.description?has_content]
      <span>${content.description?html}</span>
    [/#if]

</div>