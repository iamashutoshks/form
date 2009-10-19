[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    <input type="hidden" name="${content.controlName}" id="${content.controlName}" value="${content.value!""}"/>
</div>
