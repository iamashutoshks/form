[#assign cms=JspTaglibs["cms-taglib"]]
<!-- end form
[#if model.errorMessages?size > 0]
  <div class="text error">
    <h1>Something went wrong!</h1>
    <ul>
    ${actionResult}
    [#list model.errorMessages as i]
      <li>
        <a href="#${i}">Lorem ipsum dolor sit amet consectetuer elit.</a>
      </li>
    [/#list]
    </ul>
  </div>
[/#if]
-->
<div class="form-wrapper" >
<form id="${content.formName?default("form0")}" method="post" action="" enctype="multipart/form-data" >
[#if content.requiredMessage?has_content]
<p class="required"><span>${content.requiredSymbol?default("*")}</span> ${content.requiredMessage}</p>
[/#if]
<fieldset>
[#if content.formTitle?has_content]
  <h2>${content.formTitle}</h2>
[/#if]
  [#if content.controls?exists]
    [@cms.contentNodeIterator contentNodeCollectionName="controls"]
      [@cms.includeTemplate/]
    [/@cms.contentNodeIterator]
  [/#if]
</fieldset>
</form>
  <div>
    [@cms.newBar contentNodeCollectionName="controls" paragraph="formEdit,formGroupSelect,formRadioCheck,formSubmit,formFile" /]
  </div>
</div> <!-- end form -->


