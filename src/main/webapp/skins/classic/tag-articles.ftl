<#include "macro-head.ftl">
<#include "macro-list.ftl">
<#include "macro-pagination.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="B3log ${symphonyLabel} - ${tag.tagTitle}">
        <meta name="keywords" content="${tag.tagTitle},${tag.tagCreatorName}"/>
        <meta name="description" content="${tag.tagCreatorName},${tag.tagDescription}"/>
        </@head>
        <link type="text/css" rel="stylesheet" href="/css/index.css?${staticResourceVersion}" />
    </head>
    <body>
        <#include "header.ftl">
        <div class="main">
            <div class="wrapper fn-clear">
                <div class="content">
                    <div class="fn-clear">
                        <#if tag.tagIconPath!="">
                        <img class="avatar tag-article-img" src="${staticServePath}/images/tags/${tag.tagIconPath}">
                        </#if>
                        <h1><a rel="tag" title="${tag.tagTitle?url('UTF-8')}" href="/tags/${tag.tagTitle?url('utf-8')}">${tag.tagTitle}</a></h1>
                        ${tag.tagDescription}
                    </div>
                    <div class="fn-clear">
                        <div class="fn-left">
                            <ul class="status fn-clear">
                                <li>
                                    <strong>${tag.tagReferenceCount}</strong>
                                    <span class="ft-small">${referenceLabel}</span>
                                </li>
                                <li>
                                    <strong>${tag.tagCommentCount}</strong>
                                    <span class="ft-small">${cmtLabel}</span>
                                </li>
                            </ul>
                        </div>
                        <div class="tag-artile-user">
                            <a rel="nofollow" class="fn-left" title="${creatorLabel}:${tag.tagCreatorName}" href="/member/${tag.tagCreatorName}">
                                <img class="avatar" src="${tag.tagCreatorThumbnailURL}">
                            </a>
                            <div class="fn-right">
                                <#list tag.tagParticipants as commenter>
                                <a rel="nofollow" class="fn-left" title="${contributorLabel}:${commenter.tagParticipantName}" href="/member/${commenter.tagParticipantName}">
                                    <img class="avatar" src="${commenter.tagParticipantThumbnailURL}">
                                </a>
                                </#list>
                            </div>
                        </div>
                    </div>
                    <div class="fn-clear">
                        <@list listData=articles/>
                        <@pagination url="/tags/${tag.tagTitle?url('utf-8')}"/>
                    </div>
                </div> 
                <div class="side">
                    <#include "side.ftl">
                </div>
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
