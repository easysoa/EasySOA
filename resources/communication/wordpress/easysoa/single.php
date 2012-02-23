<?php get_header(); ?>

<div id="left">
<?php if (have_posts()) : while (have_posts()) : the_post(); ?>

<h2><?php edit_post_link('[e]','<b>','</b> '); ?><?php the_title(); ?></h2>
<p class="timestamp"><?php the_time('j F, Y (H:i)') ?> | <?php the_category(', ') ?> | By: <?php the_author(); ?></p>
<div class="contenttext">
<?php the_content('<p>Read more &raquo;</p>'); ?>
</div>

<?php link_pages('<p><strong>Pages:<strong> ', '</p>', 'number'); ?>
<?php the_tags('<p class="postmeta">Tags: ', ', ', '</p>'); ?>

<div id="postnav">
<?php previous_post_link('<p>&laquo; %link</p>') ?><?php next_post_link('<p class="right">%link &raquo;</p>') ?>
</div>

<?php comments_template(); ?>

<?php endwhile; else: ?>
<p>Entry could not be found!</p>
<?php endif; ?>
</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
