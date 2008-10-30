[#assign cms=JspTaglibs["cms-taglib"]]


<div class="button-wrapper" >
[@cms.adminOnly]
    [@cms.editBar /]
[/@cms.adminOnly]
    <input name="${content.controlName}" id="${content.controlName}" type="submit" value="${content.buttonText}" />
</div>

