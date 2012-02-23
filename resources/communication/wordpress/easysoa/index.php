<?php get_header(); ?>

<div id="left">
<?php if (have_posts()) : ?><?php while (have_posts()) : the_post(); ?>

<div class="post">
<h2><?php edit_post_link('[e]','<b>','</b> '); ?><a href="<?php the_permalink() ?>" title="<?php the_title(); ?>"><?php the_title(); ?></a></h2>
<p class="timestamp"><?php the_time('j F, Y (H:i)') ?> | <?php the_category(', ') ?> | By: <?php the_author(); ?></p>

<div class="contenttext">
<?php the_content('Read more &raquo;'); ?>
</div>

<p class="postmeta"><?php the_tags('Tags: ', ', ', ' | '); ?>
<?php comments_popup_link( 'No comments', '1 comment', '% comments', '', ''); ?></p>
</div>

<?php endwhile; ?>

<div id="postnav">
<p><?php next_posts_link('&laquo; Older entries') ?></p>
<p class="right"><?php previous_posts_link('Newer entries &raquo;') ?></p>
</div>
		
<?php else : ?>
<h2>Page can not be found!</h2>
<p>The article or page that you are trying to reach can not be found. It may have been deleted or moved. Please use the navigation menus or the search box to look elsewhere.</p>
<?php endif; ?>

</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
