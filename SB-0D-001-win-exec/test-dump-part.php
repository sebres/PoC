<?php
$args = array_slice($argv, 1);
$args = implode(" ", array_map(function ($v) { return escapeshellarg($v); }, $args));
// print(exec($args));
system($args);
?>