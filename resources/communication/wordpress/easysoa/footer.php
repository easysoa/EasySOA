    </div>
  </div>
  <div id="bottom">
    <div id="bottomContent">
    
      <?php if ( function_exists('dynamic_sidebar') ) dynamic_sidebar('Bottom menu') ?>
      <!--<ul>
        <li><b>EasySOA</b> is blablabla</li>
        <li>
          &copy; <?php echo date('Y'); ?> <?php bloginfo('name'); ?><br />
          <a href="<?php bloginfo('rss2_url'); ?>">Entries</a> | <a href="<?php bloginfo('comments_rss2_url'); ?>">Comments</a> | <?php wp_loginout(); ?><br />
          Powered by <a href="http://wordpress.org/">WordPress</a>
        </li>
      </ul>
      
      <ul>
        <li><a href="#">Link 1</a></li>
        <li><a href="#">Link 2</a></li>
        <li><a href="#">Link 3</a></li>
        <li><a href="#">Link 4</a></li>
        <li><a href="#">Link 5</a></li>
      </ul>
      <ul>
        <li><a href="#">Link 1</a></li>
        <li><a href="#">Link 2</a></li>
      </ul>-->
      <?php do_action('wp_footer'); ?>
    </div>
  </div>
</body>

</html>
