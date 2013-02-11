[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    <input type="hidden" name="${content.controlName?html}" id="${content.controlName?html}" value="${(content.value?html)!""}"/>
</div>
