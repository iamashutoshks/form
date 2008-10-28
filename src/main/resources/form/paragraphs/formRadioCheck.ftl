[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.adminOnly]
    <div >[@cms.editBar /]</div>
[/@cms.adminOnly]
[#if content.title?has_content]
	<h4>${content.title}</h4>
[/#if]
[#if content.legend?has_content]
<fieldset ${content.horizontal?string("class=\"mod\"", "")} >
  <legend>${content.legend}</legend>
[/#if]
[#assign values=content.values?split("\r\n")]
  [#list content.labels?split("\r\n") as label]
    <label for="${content.controlName}_${label_index}">
    <input id="${content.controlName}_${label_index}" name="${content.controlName}" type="${content.type}" value="${values[label_index]}"/>
     ${label}
    </label>
  [/#list]


[#if content.legend?has_content]
</fieldset>
[/#if]
</div>
