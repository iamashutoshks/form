[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!}>
[@cms.adminOnly]
    [@cms.editBar /]
[/@cms.adminOnly]
    [#if content.title?has_content]
      <label for="${content.controlName}">
        ${content.title}
    [/#if]
        <input type="file" name="${content.controlName}" id="${content.controlName}" value="${model.value!}"/>
    [#if content.title?has_content]
      </label>
    [/#if]
  </div>
