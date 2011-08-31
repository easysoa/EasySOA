<form method="get" id="searchform" action="<?php echo $_SERVER['PHP_SELF']; ?>">
  <div class="searchbox">
    <input type="text" value="<?php echo wp_specialchars($s, 1); ?>" name="s" id="s" size="14" />
    <input type="image" src="<?php bloginfo('template_url'); ?>/img/search.png" id="searchsubmit" value="Search" />
  </div>
</form>
