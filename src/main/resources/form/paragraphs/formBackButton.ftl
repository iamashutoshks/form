[#assign cms=JspTaglibs["cms-taglib"]]


<div class="button-wrapper" >
[@cms.editBar /]
    <input type="button" value="${content.buttonText!"Back"?html}" onclick="history.go(-1);return false;"/>
</div>

