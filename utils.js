'use strict';

const http = require('http');
const https = require('https');

// ============================================================
// SHARED UTILITIES
// Small, dependency-free helpers reused across the bot.
// ============================================================

// Pick the correct Node protocol module for a URL.
function getProtocol(url) {
  return String(url).startsWith('https') ? https : http;
}

// Random non-negative integer in the half-open range [0, range).
function randInt(range) {
  return Math.floor(Math.random() * range);
}

// Random integer offset from a base: base + [0, range).
function randDelay(base, range) {
  return base + randInt(range);
}

module.exports = { getProtocol, randInt, randDelay };
