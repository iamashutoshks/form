

[#if cmsfn.editMode]
    <div class="condition-list">

        <div style="clear: both" >
            [#-- FIXME the former label was 'newLabel="${i18n['condition.newLabel']}"' --]
            [@cms.edit/]
        </div>

        [#list components as component ]
            [@cms.render content=component /]
        [/#list]

    </div><!-- end condition-list -->
[/#if]