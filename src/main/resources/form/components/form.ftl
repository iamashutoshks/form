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

    [#assign page = model.root.content]
    [#assign title = content.formTitle!page.title!page.@name]

    <div class="text">
        <h1>${title}</h1>
        [#if content.formText?has_content]
            <p>${content.formText!}</p>
        [/#if]
        [#if model.stepNavigation?has_content]
            <div id="step-by-step">
                <ol>
                    [#list model.stepNavigation as item]
                        <li class="done"><a href="${item.href!}">${item.navigationTitle!}</a></li>
                    [/#list]
                    <li><strong><em>${i18n['nav.selected']} </em>${content.navigationTitle!content.formTitle!content.@name}</strong></li>
                </ol>
            </div><!-- end step-by-step -->
        [/#if]
    </div><!-- end text -->

    <div class="form-wrapper" >
        <form id="${content.formName?default("form0")}" method="post" action="" enctype="${def.parameters.formEnctype?default("multipart/form-data")}" >
            <div class="form-item-hidden">
                <input type="hidden" name="mgnlModelExecutionUUID" value="${content.@id}" />
                <input type="hidden" name="field" value="" />
                [#if model.formState?has_content]
                    <input type="hidden" name="mgnlFormToken" value="${model.formState.token!}" />
                [/#if]
            </div><!-- end form-item-hidden -->

            [@cms.area name="fieldsets"/]
        </form>
    </div><!-- end form-wrapper -->

[/#if]
