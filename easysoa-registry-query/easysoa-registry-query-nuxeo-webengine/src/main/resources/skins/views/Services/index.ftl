<@extends src="base.ftl">

<@block name="content">

<div class="btn-toolbar">
    <form class="form-inline pull-right" action="#">
        <label>Filter </label>
        <input type="text" id="docFilter" class="search-query" />
    </form>
    <div class="btn-group">
        <button id="buttonParent" class="btn btn-primary">Back to parent</button>
    </div>
    <div class="btn-group">
        <button id="buttonSort" class="btn">Sort by name</button>
    	<button id="buttonShuffle" class="btn">Shuffle</button>
	</div>
</div>
	
<div class="row well" id="documents"></div>

<!-- Templates -->

<script type="text/template" id="document-template-small">
    <div class="document doctype-<%= style %>">
        <div class="document-key hidden"><%= key %></div>
        <div class="btn btn-large zoom-button zoom-in">
            <i class="icon-plus zoom-in"></i>
        </div>
        <div class="document-link">
            <ul class="nav nav-list">
                <%= templateCommon %>
            </ul>
        </div>
    </div>
</script>

<script type="text/template" id="document-template-big">
    <div class="document document-big doctype-<%= style %>">
        <div class="document-key hidden"><%= key %></div>
        <div class="btn btn-large zoom-button zoom-out">
            <i class="icon-minus zoom-out"></i>
        </div>
        <div class="document-link">
            <ul class="nav nav-list">
                <%= templateCommon %>
                <li><div class="document-label">&nbsp;</div><div class="document-value">&nbsp;</div></li>
                <li><div class="document-label">Desc.</div><div class="document-value"><%= properties['dc:description'] %></div></li>
            </ul>
        </div>
    </div>
</script>

<script type="text/template" id="document-template-common">
    <li>
        <div class="document-icon"><img src="${skinPath}/img/<%= style %>.gif" /></div>
        <div class="document-title"><%= title %></div>
    </li>
    <li><div class="document-label">Type</div><div class="document-value"><%= type %></div></li>
    <li><div class="document-label">Last edit</div><div class="document-value"><%= lastModified.substring(0, 10) %></div></li>
    <li><div class="document-label">State</div><div class="document-value"><%= state %></div></li>
</script>

</@block>

</@extends>