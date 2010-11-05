[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!}>
[@cms.editBar /]
    [#if content.title?has_content]
        <label for="${content.controlName}">
            ${mgnl.encode(content).title!}
    	</label>
    [/#if]
        <input type="file" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}"/>

</div>
