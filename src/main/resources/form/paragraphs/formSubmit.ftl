[#assign cms=JspTaglibs["cms-taglib"]]


<div class="button-wrapper" >
[@cms.editBar /]
    <input name="${content.controlName}" id="${content.controlName}" type="submit" value="${content.buttonText}" />
</div>

