<style type="text/css">
/* Breadcrumb */
#form-breadcrumb {
  margin: 0 0 20px 0;
  float: left;
  width: 100%;
}

#form-breadcrumb ol {
  list-style: none;
  margin: 0;
}

#form-breadcrumb li {
  float: left;
  background: url(${ctx.contextPath}/resources/templating-kit/themes/pop/img/icons/sprites.png) 0 -1010px no-repeat;
  border: none;
  margin: 0 6px 0 0;
  padding: 0 0 0 7px;
  font-size: 120%;
  font-weight: normal;
}

#form-breadcrumb {
  position: relative;
  left: 0;
  top: 0;
}
</style>

[#if actionResult == "go-to-first-page"]
    <div class="text">
        ${i18n.get("form.user.errorMessage.go-to-first-page", [cmsfn.link("website", model.view.firstPage)])}
    </div><!-- end text -->

[#elseif actionResult == "success"]
    <div class="text success">
        <h1>${model.view.successTitle!i18n['form.default.successTitle']}</h1>
        <p>${model.view.successMessage!}</p>
    </div><!-- end text success -->

[#elseif actionResult == "session-expired"]
    <div class="text error">
        ${i18n.get("form.user.errorMessage.session-expired", [cmsfn.link("website", model.view.firstPage)])}
    </div><!-- end text error -->

[#elseif actionResult == "failure"]
    <div class="text error">
        <ul>
            <li>${model.view.errorMessage}</li>
        </ul>
    </div><!-- end text error -->

[#else]
    [#if model.view.validationErrors?size > 0]
        <div class="text error">
            <h1>${model.view.errorTitle!i18n['form.default.errorTitle']}</h1>
            <ul>
                [#assign keys = model.view.validationErrors?keys]
                [#list keys as key]
                    <li>
                        <a href="#${key}_label">${model.view.validationErrors[key]!}</a>
                    </li>
                [/#list]
            </ul>
        </div><!-- end text error -->
    [/#if]

    [@cms.edit/]

    <div class="text">
        <h1>${content.formTitle!}</h1>
        <p>${content.formText!}</p>
        [#if model.breadcrumb?has_content ]
            <div id="form-breadcrumb">
                <ol>
                    [#list model.breadcrumb as item]
                        <li><a href="${item.href!}">${item.navigationTitle!}</a></li>
                    [/#list]
                </ol>
            </div><!-- end form-breadcrumb -->
        [/#if]
    </div><!-- end text -->

    <div class="form-wrapper" >
        <form id="${content.formName?default("form0")}" method="post" action="" enctype="${def.parameters.formEnctype?default("multipart/form-data")}" >
            <div class="form-item-hidden">
                <input type="hidden" name="mgnlModelExecutionUUID" value="${content.@uuid}" />
                <input type="hidden" name="field" value="" />
                [#if model.formState?has_content]
                    <input type="hidden" name="mgnlFormToken" value="${model.formState.token}" />
                [/#if]
            </div><!-- end form-item-hidden -->

            [@cms.area name="fieldsets"/]
        </form>
    </div><!-- end form-wrapper -->

[/#if]