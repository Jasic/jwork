/*! Built with IMPACT - impactjs.com */
(function (g) {
    Number.prototype.map = function (i, j, h, k) {
        return h + (k - h) * ((this - i) / (j - i))
    };
    Number.prototype.limit = function (i, h) {
        return Math.min(h, Math.max(i, this))
    };
    Number.prototype.round = function (h) {
        h = Math.pow(10, h || 0);
        return Math.round(this * h) / h
    };
    Number.prototype.floor = function () {
        return Math.floor(this)
    };
    Number.prototype.ceil = function () {
        return Math.ceil(this)
    };
    Number.prototype.toInt = function () {
        return (this | 0)
    };
    Number.prototype.toRad = function () {
        return (this / 180) * Math.PI
    };
    Number.prototype.toDeg = function () {
        return (this * 180) / Math.PI
    };
    Object.defineProperty(Array.prototype, "erase", {
        value: function (j) {
            for (var h = this.length; h--;) {
                if (this[h] === j) {
                    this.splice(h, 1)
                }
            }
            return this
        }
    });
    Object.defineProperty(Array.prototype, "random", {
        value: function (h) {
            return this[Math.floor(Math.random() * this.length)]
        }
    });
    Function.prototype.bind = Function.prototype.bind || function (h) {
        if (typeof this !== "function") {
            throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable")
        }
        var l = Array.prototype.slice.call(arguments, 1), k = this, i = function () {
        }, j = function () {
            return k.apply((this instanceof i && h ? this : h), l.concat(Array.prototype.slice.call(arguments)))
        };
        i.prototype = this.prototype;
        j.prototype = new i();
        return j
    };
    g.ig = {
        game: null,
        debug: null,
        version: "1.24",
        global: g,
        modules: {},
        resources: [],
        ready: false,
        baked: false,
        nocache: "",
        ua: {},
        prefix: (g.ImpactPrefix || ""),
        lib: "lib/",
        _current: null,
        _loadQueue: [],
        _waitForOnload: 0,
        $: function (h) {
            return h.charAt(0) == "#" ? document.getElementById(h.substr(1)) : document.getElementsByTagName(h)
        },
        $new: function (h) {
            return document.createElement(h)
        },
        copy: function (j) {
            if (!j || typeof(j) != "object" || j instanceof HTMLElement || j instanceof ig.Class) {
                return j
            } else {
                if (j instanceof Array) {
                    var m = [];
                    for (var k = 0, h = j.length; k < h; k++) {
                        m[k] = ig.copy(j[k])
                    }
                    return m
                } else {
                    var m = {};
                    for (var k in j) {
                        m[k] = ig.copy(j[k])
                    }
                    return m
                }
            }
        },
        merge: function (j, h) {
            for (var i in h) {
                var k = h[i];
                if (typeof(k) != "object" || k instanceof HTMLElement || k instanceof ig.Class || k === null) {
                    j[i] = k
                } else {
                    if (!j[i] || typeof(j[i]) != "object") {
                        j[i] = (k instanceof Array) ? [] : {}
                    }
                    ig.merge(j[i], k)
                }
            }
            return j
        },
        ksort: function (l) {
            if (!l || typeof(l) != "object") {
                return []
            }
            var k = [], h = [];
            for (var j in l) {
                k.push(j)
            }
            k.sort();
            for (var j = 0; j < k.length; j++) {
                h.push(l[k[j]])
            }
            return h
        },
        setVendorAttribute: function (j, h, k) {
            var i = h.charAt(0).toUpperCase() + h.substr(1);
            j[h] = j["ms" + i] = j["moz" + i] = j["webkit" + i] = j["o" + i] = k
        },
        getVendorAttribute: function (j, h) {
            var i = h.charAt(0).toUpperCase() + h.substr(1);
            return j[h] || j["ms" + i] || j["moz" + i] || j["webkit" + i] || j["o" + i]
        },
        normalizeVendorAttribute: function (j, h) {
            var i = ig.getVendorAttribute(j, h);
            if (!j[h] && i) {
                j[h] = i
            }
        },
        getImagePixels: function (j, n, m, h, p) {
            var i = ig.$new("canvas");
            i.width = j.width;
            i.height = j.height;
            var q = i.getContext("2d");
            ig.System.SCALE.CRISP(i, q);
            var l = ig.getVendorAttribute(q, "backingStorePixelRatio") || 1;
            ig.normalizeVendorAttribute(q, "getImageDataHD");
            var o = j.width / l, k = j.height / l;
            i.width = Math.ceil(o);
            i.height = Math.ceil(k);
            q.drawImage(j, 0, 0, o, k);
            return (l === 1) ? q.getImageData(n, m, h, p) : q.getImageDataHD(n, m, h, p)
        },
        module: function (h) {
            if (ig._current) {
                throw ("Module '" + ig._current.name + "' defines nothing")
            }
            if (ig.modules[h] && ig.modules[h].body) {
                throw ("Module '" + h + "' is already defined")
            }
            ig._current = {name: h, requires: [], loaded: false, body: null};
            ig.modules[h] = ig._current;
            ig._loadQueue.push(ig._current);
            return ig
        },
        requires: function () {
            ig._current.requires = Array.prototype.slice.call(arguments);
            return ig
        },
        defines: function (h) {
            ig._current.body = h;
            ig._current = null;
            ig._initDOMReady()
        },
        addResource: function (h) {
            ig.resources.push(h)
        },
        setNocache: function (h) {
            ig.nocache = h ? "?" + Date.now() : ""
        },
        log: function () {
        },
        assert: function (i, h) {
        },
        show: function (h, i) {
        },
        mark: function (i, h) {
        },
        _loadScript: function (j, i) {
            ig.modules[j] = {name: j, requires: [], loaded: false, body: null};
            ig._waitForOnload++;
            var k = ig.prefix + ig.lib + j.replace(/\./g, "/") + ".js" + ig.nocache;
            var h = ig.$new("script");
            h.type = "text/javascript";
            h.src = k;
            h.onload = function () {
                ig._waitForOnload--;
                ig._execModules()
            };
            h.onerror = function () {
                throw ("Failed to load module " + j + " at " + k + " " + "required from " + i)
            };
            ig.$("head")[0].appendChild(h)
        },
        _execModules: function () {
            var k = false;
            for (var p = 0; p < ig._loadQueue.length; p++) {
                var n = ig._loadQueue[p];
                var h = true;
                for (var o = 0; o < n.requires.length; o++) {
                    var l = n.requires[o];
                    if (!ig.modules[l]) {
                        h = false;
                        ig._loadScript(l, n.name)
                    } else {
                        if (!ig.modules[l].loaded) {
                            h = false
                        }
                    }
                }
                if (h && n.body) {
                    ig._loadQueue.splice(p, 1);
                    n.loaded = true;
                    n.body();
                    k = true;
                    p--
                }
            }
            if (k) {
                ig._execModules()
            } else {
                if (!ig.baked && ig._waitForOnload == 0 && ig._loadQueue.length != 0) {
                    var q = [];
                    for (var p = 0; p < ig._loadQueue.length; p++) {
                        var s = [];
                        var r = ig._loadQueue[p].requires;
                        for (var o = 0; o < r.length; o++) {
                            var n = ig.modules[r[o]];
                            if (!n || !n.loaded) {
                                s.push(r[o])
                            }
                        }
                        q.push(ig._loadQueue[p].name + " (requires: " + s.join(", ") + ")")
                    }
                    throw ("Unresolved (or circular?) dependencies. " + "Most likely there's a name/path mismatch for one of the listed modules " + "or a previous syntax error prevents a module from loading:\n" + q.join("\n"))
                }
            }
        },
        _DOMReady: function () {
            if (!ig.modules["dom.ready"].loaded) {
                if (!document.body) {
                    return setTimeout(ig._DOMReady, 13)
                }
                ig.modules["dom.ready"].loaded = true;
                ig._waitForOnload--;
                ig._execModules()
            }
            return 0
        },
        _boot: function () {
            if (document.location.href.match(/\?nocache/)) {
                ig.setNocache(true)
            }
            ig.ua.pixelRatio = g.devicePixelRatio || 1;
            ig.ua.viewport = {width: g.innerWidth, height: g.innerHeight};
            ig.ua.screen = {
                width: g.screen.availWidth * ig.ua.pixelRatio,
                height: g.screen.availHeight * ig.ua.pixelRatio
            };
            ig.ua.iPhone = /iPhone/i.test(navigator.userAgent);
            ig.ua.iPhone4 = (ig.ua.iPhone && ig.ua.pixelRatio == 2);
            ig.ua.iPad = /iPad/i.test(navigator.userAgent);
            ig.ua.android = /android/i.test(navigator.userAgent);
            ig.ua.winPhone = /Windows Phone/i.test(navigator.userAgent);
            ig.ua.iOS = ig.ua.iPhone || ig.ua.iPad;
            ig.ua.mobile = ig.ua.iOS || ig.ua.android || ig.ua.winPhone || /mobile/i.test(navigator.userAgent);
            ig.ua.touchDevice = (("ontouchstart" in g) || (g.navigator.msMaxTouchPoints))
        },
        _initDOMReady: function () {
            if (ig.modules["dom.ready"]) {
                ig._execModules();
                return
            }
            ig._boot();
            ig.modules["dom.ready"] = {requires: [], loaded: false, body: null};
            ig._waitForOnload++;
            if (document.readyState === "complete") {
                ig._DOMReady()
            } else {
                document.addEventListener("DOMContentLoaded", ig._DOMReady, false);
                g.addEventListener("load", ig._DOMReady, false)
            }
        }
    };
    ig.normalizeVendorAttribute(g, "requestAnimationFrame");
    if (g.requestAnimationFrame) {
        var e = 1, a = {};
        g.ig.setAnimation = function (k, i) {
            var j = e++;
            a[j] = true;
            var h = function () {
                if (!a[j]) {
                    return
                }
                g.requestAnimationFrame(h, i);
                k()
            };
            g.requestAnimationFrame(h, i);
            return j
        };
        g.ig.clearAnimation = function (h) {
            delete a[h]
        }
    } else {
        g.ig.setAnimation = function (i, h) {
            return g.setInterval(i, 1000 / 60)
        };
        g.ig.clearAnimation = function (h) {
            g.clearInterval(h)
        }
    }
    var c = false, f = /xyz/.test(function () {
        xyz
    }) ? /\bparent\b/ : /.*/;
    var b = 0;
    g.ig.Class = function () {
    };
    var d = function (k) {
        var j = this.prototype;
        var i = {};
        for (var h in k) {
            if (typeof(k[h]) == "function" && typeof(j[h]) == "function" && f.test(k[h])) {
                i[h] = j[h];
                j[h] = (function (l, m) {
                    return function () {
                        var o = this.parent;
                        this.parent = i[l];
                        var n = m.apply(this, arguments);
                        this.parent = o;
                        return n
                    }
                })(h, k[h])
            } else {
                j[h] = k[h]
            }
        }
    };
    g.ig.Class.extend = function (l) {
        var k = this.prototype;
        c = true;
        var j = new this();
        c = false;
        for (var i in l) {
            if (typeof(l[i]) == "function" && typeof(k[i]) == "function" && f.test(l[i])) {
                j[i] = (function (m, n) {
                    return function () {
                        var p = this.parent;
                        this.parent = k[m];
                        var o = n.apply(this, arguments);
                        this.parent = p;
                        return o
                    }
                })(i, l[i])
            } else {
                j[i] = l[i]
            }
        }
        function h() {
            if (!c) {
                if (this.staticInstantiate) {
                    var n = this.staticInstantiate.apply(this, arguments);
                    if (n) {
                        return n
                    }
                }
                for (var m in this) {
                    if (typeof(this[m]) == "object") {
                        this[m] = ig.copy(this[m])
                    }
                }
                if (this.init) {
                    this.init.apply(this, arguments)
                }
            }
            return this
        }

        h.prototype = j;
        h.prototype.constructor = h;
        h.extend = g.ig.Class.extend;
        h.inject = d;
        h.classId = j.classId = ++b;
        return h
    };
    if (g.ImpactMixin) {
        ig.merge(ig, g.ImpactMixin)
    }
})(window);
ig.baked = true;
ig.module("impact.image").defines(function () {
    ig.Image = ig.Class.extend({
        data: null,
        width: 0,
        height: 0,
        loaded: false,
        failed: false,
        loadCallback: null,
        path: "",
        staticInstantiate: function (a) {
            return ig.Image.cache[a] || null
        },
        init: function (a) {
            this.path = a;
            this.load()
        },
        load: function (a) {
            if (this.loaded) {
                if (a) {
                    a(this.path, true)
                }
                return
            } else {
                if (!this.loaded && ig.ready) {
                    this.loadCallback = a || null;
                    this.data = new Image();
                    this.data.onload = this.onload.bind(this);
                    this.data.onerror = this.onerror.bind(this);
                    this.data.src = ig.prefix + this.path + ig.nocache
                } else {
                    ig.addResource(this)
                }
            }
            ig.Image.cache[this.path] = this
        },
        reload: function () {
            this.loaded = false;
            this.data = new Image();
            this.data.onload = this.onload.bind(this);
            this.data.src = this.path + "?" + Date.now()
        },
        onload: function (a) {
            this.width = this.data.width;
            this.height = this.data.height;
            this.loaded = true;
            if (ig.system.scale != 1) {
                this.resize(ig.system.scale)
            }
            if (this.loadCallback) {
                this.loadCallback(this.path, true)
            }
        },
        onerror: function (a) {
            this.failed = true;
            if (this.loadCallback) {
                this.loadCallback(this.path, false)
            }
        },
        resize: function (b) {
            var c = ig.getImagePixels(this.data, 0, 0, this.width, this.height);
            var a = this.width * b;
            var k = this.height * b;
            var d = ig.$new("canvas");
            d.width = a;
            d.height = k;
            var i = d.getContext("2d");
            var j = i.getImageData(0, 0, a, k);
            for (var g = 0; g < k; g++) {
                for (var h = 0; h < a; h++) {
                    var f = (Math.floor(g / b) * this.width + Math.floor(h / b)) * 4;
                    var e = (g * a + h) * 4;
                    j.data[e] = c.data[f];
                    j.data[e + 1] = c.data[f + 1];
                    j.data[e + 2] = c.data[f + 2];
                    j.data[e + 3] = c.data[f + 3]
                }
            }
            i.putImageData(j, 0, 0);
            this.data = d
        },
        draw: function (d, c, f, e, b, a) {
            if (!this.loaded) {
                return
            }
            var g = ig.system.scale;
            f = f ? f * g : 0;
            e = e ? e * g : 0;
            b = (b ? b : this.width) * g;
            a = (a ? a : this.height) * g;
            ig.system.context.drawImage(this.data, f, e, b, a, ig.system.getDrawPos(d), ig.system.getDrawPos(c), b, a);
            ig.Image.drawCount++
        },
        drawTile: function (g, f, d, h, c, l, k) {
            c = c ? c : h;
            if (!this.loaded || h > this.width || c > this.height) {
                return
            }
            var b = ig.system.scale;
            var a = Math.floor(h * b);
            var e = Math.floor(c * b);
            var j = l ? -1 : 1;
            var i = k ? -1 : 1;
            if (l || k) {
                ig.system.context.save();
                ig.system.context.scale(j, i)
            }
            ig.system.context.drawImage(this.data, (Math.floor(d * h) % this.width) * b, (Math.floor(d * h / this.width) * c) * b, a, e, ig.system.getDrawPos(g) * j - (l ? a : 0), ig.system.getDrawPos(f) * i - (k ? e : 0), a, e);
            if (l || k) {
                ig.system.context.restore()
            }
            ig.Image.drawCount++
        }
    });
    ig.Image.drawCount = 0;
    ig.Image.cache = {};
    ig.Image.reloadCache = function () {
        for (var a in ig.Image.cache) {
            ig.Image.cache[a].reload()
        }
    }
});
ig.baked = true;
ig.module("impact.font").requires("impact.image").defines(function () {
    ig.Font = ig.Image.extend({
        widthMap: [],
        indices: [],
        firstChar: 32,
        alpha: 1,
        letterSpacing: 1,
        lineSpacing: 0,
        onload: function (a) {
            this._loadMetrics(this.data);
            this.parent(a)
        },
        widthForString: function (d) {
            if (d.indexOf("\n") !== -1) {
                var a = d.split("\n");
                var c = 0;
                for (var b = 0; b < a.length; b++) {
                    c = Math.max(c, this._widthForLine(a[b]))
                }
                return c
            } else {
                return this._widthForLine(d)
            }
        },
        _widthForLine: function (c) {
            var b = 0;
            for (var a = 0; a < c.length; a++) {
                b += this.widthMap[c.charCodeAt(a) - this.firstChar] + this.letterSpacing
            }
            return b
        },
        heightForString: function (a) {
            return a.split("\n").length * (this.height + this.lineSpacing)
        },
        draw: function (j, g, f, d) {
            if (typeof(j) != "string") {
                j = j.toString()
            }
            if (j.indexOf("\n") !== -1) {
                var k = j.split("\n");
                var h = this.height + this.lineSpacing;
                for (var b = 0; b < k.length; b++) {
                    this.draw(k[b], g, f + b * h, d)
                }
                return
            }
            if (d == ig.Font.ALIGN.RIGHT || d == ig.Font.ALIGN.CENTER) {
                var a = this._widthForLine(j);
                g -= d == ig.Font.ALIGN.CENTER ? a / 2 : a
            }
            if (this.alpha !== 1) {
                ig.system.context.globalAlpha = this.alpha
            }
            for (var b = 0; b < j.length; b++) {
                var e = j.charCodeAt(b);
                g += this._drawChar(e - this.firstChar, g, f)
            }
            if (this.alpha !== 1) {
                ig.system.context.globalAlpha = 1
            }
            ig.Image.drawCount += j.length
        },
        _drawChar: function (i, e, d) {
            if (!this.loaded || i < 0 || i >= this.indices.length) {
                return 0
            }
            var g = ig.system.scale;
            var b = this.indices[i] * g;
            var h = 0;
            var a = this.widthMap[i] * g;
            var f = (this.height - 2) * g;
            ig.system.context.drawImage(this.data, b, h, a, f, ig.system.getDrawPos(e), ig.system.getDrawPos(d), a, f);
            return this.widthMap[i] + this.letterSpacing
        },
        _loadMetrics: function (f) {
            this.height = f.height - 1;
            this.widthMap = [];
            this.indices = [];
            var e = ig.getImagePixels(f, 0, f.height - 1, f.width, 1);
            var b = 0;
            var d = 0;
            for (var a = 0; a < f.width; a++) {
                var c = a * 4 + 3;
                if (e.data[c] > 127) {
                    d++
                } else {
                    if (e.data[c] < 128 && d) {
                        this.widthMap.push(d);
                        this.indices.push(a - d);
                        b++;
                        d = 0
                    }
                }
            }
            this.widthMap.push(d);
            this.indices.push(a - d)
        }
    });
    ig.Font.ALIGN = {LEFT: 0, RIGHT: 1, CENTER: 2}
});
ig.baked = true;
ig.module("impact.sound").defines(function () {
    ig.SoundManager = ig.Class.extend({
        clips: {}, volume: 1, format: null, init: function () {
            if (!ig.Sound.enabled || !window.Audio) {
                ig.Sound.enabled = false;
                return
            }
            var a = new Audio();
            for (var b = 0; b < ig.Sound.use.length; b++) {
                var c = ig.Sound.use[b];
                if (a.canPlayType(c.mime)) {
                    this.format = c;
                    break
                }
            }
            if (!this.format) {
                ig.Sound.enabled = false
            }
            if (ig.Sound.enabled && ig.Sound.useWebAudio) {
                this.audioContext = new AudioContext();
                this.boundWebAudioUnlock = this.unlockWebAudio.bind(this);
                document.addEventListener("touchstart", this.boundWebAudioUnlock, false)
            }
        }, unlockWebAudio: function () {
            document.removeEventListener("touchstart", this.boundWebAudioUnlock, false);
            var a = this.audioContext.createBuffer(1, 1, 22050);
            var b = this.audioContext.createBufferSource();
            b.buffer = a;
            b.connect(this.audioContext.destination);
            b.start(0)
        }, load: function (c, b, a) {
            if (b && ig.Sound.useWebAudio) {
                return this.loadWebAudio(c, b, a)
            } else {
                return this.loadHTML5Audio(c, b, a)
            }
        }, loadWebAudio: function (g, f, a) {
            var e = ig.prefix + g.replace(/[^\.]+$/, this.format.ext) + ig.nocache;
            if (this.clips[g]) {
                return this.clips[g]
            }
            var b = new ig.Sound.WebAudioSource();
            this.clips[g] = b;
            var d = new XMLHttpRequest();
            d.open("GET", e, true);
            d.responseType = "arraybuffer";
            var c = this;
            d.onload = function (h) {
                c.audioContext.decodeAudioData(d.response, function (i) {
                    b.buffer = i;
                    a(g, true, h)
                }, function (i) {
                    a(g, false, i)
                })
            };
            d.onerror = function (h) {
                a(g, false, h)
            };
            d.send();
            return b
        }, loadHTML5Audio: function (j, h, d) {
            var g = ig.prefix + j.replace(/[^\.]+$/, this.format.ext) + ig.nocache;
            if (this.clips[j]) {
                if (this.clips[j] instanceof ig.Sound.WebAudioSource) {
                    return this.clips[j]
                }
                if (h && this.clips[j].length < ig.Sound.channels) {
                    for (var e = this.clips[j].length; e < ig.Sound.channels; e++) {
                        var c = new Audio(g);
                        c.load();
                        this.clips[j].push(c)
                    }
                }
                return this.clips[j][0]
            }
            var f = new Audio(g);
            if (d) {
                if (ig.ua.mobile) {
                    setTimeout(function () {
                        d(j, true, null)
                    }, 0)
                } else {
                    f.addEventListener("canplaythrough", function b(a) {
                        f.removeEventListener("canplaythrough", b, false);
                        d(j, true, a)
                    }, false);
                    f.addEventListener("error", function (a) {
                        d(j, false, a)
                    }, false)
                }
            }
            f.preload = "auto";
            f.load();
            this.clips[j] = [f];
            if (h) {
                for (var e = 1; e < ig.Sound.channels; e++) {
                    var c = new Audio(g);
                    c.load();
                    this.clips[j].push(c)
                }
            }
            return f
        }, get: function (d) {
            var a = this.clips[d];
            if (a && a instanceof ig.Sound.WebAudioSource) {
                return a
            }
            for (var b = 0, c; c = a[b++];) {
                if (c.paused || c.ended) {
                    if (c.ended) {
                        c.currentTime = 0
                    }
                    return c
                }
            }
            a[0].pause();
            a[0].currentTime = 0;
            return a[0]
        }
    });
    ig.Music = ig.Class.extend({
        tracks: [],
        namedTracks: {},
        currentTrack: null,
        currentIndex: 0,
        random: false,
        _volume: 1,
        _loop: false,
        _fadeInterval: 0,
        _fadeTimer: null,
        _endedCallbackBound: null,
        init: function () {
            this._endedCallbackBound = this._endedCallback.bind(this);
            Object.defineProperty(this, "volume", {get: this.getVolume.bind(this), set: this.setVolume.bind(this)});
            Object.defineProperty(this, "loop", {get: this.getLooping.bind(this), set: this.setLooping.bind(this)})
        },
        add: function (d, b) {
            if (!ig.Sound.enabled) {
                return
            }
            var c = d instanceof ig.Sound ? d.path : d;
            var a = ig.soundManager.load(c, false);
            if (a instanceof ig.Sound.WebAudioSource) {
                ig.system.stopRunLoop();
                throw ("Sound '" + c + "' loaded as Multichannel but used for Music. " + "Set the multiChannel param to false when loading, e.g.: new ig.Sound(path, false)")
            }
            a.loop = this._loop;
            a.volume = this._volume;
            a.addEventListener("ended", this._endedCallbackBound, false);
            this.tracks.push(a);
            if (b) {
                this.namedTracks[b] = a
            }
            if (!this.currentTrack) {
                this.currentTrack = a
            }
        },
        next: function () {
            if (!this.tracks.length) {
                return
            }
            this.stop();
            this.currentIndex = this.random ? Math.floor(Math.random() * this.tracks.length) : (this.currentIndex + 1) % this.tracks.length;
            this.currentTrack = this.tracks[this.currentIndex];
            this.play()
        },
        pause: function () {
            if (!this.currentTrack) {
                return
            }
            this.currentTrack.pause()
        },
        stop: function () {
            if (!this.currentTrack) {
                return
            }
            this.currentTrack.pause();
            this.currentTrack.currentTime = 0
        },
        play: function (b) {
            if (b && this.namedTracks[b]) {
                var a = this.namedTracks[b];
                if (a != this.currentTrack) {
                    this.stop();
                    this.currentTrack = a
                }
            } else {
                if (!this.currentTrack) {
                    return
                }
            }
            this.currentTrack.play()
        },
        getLooping: function () {
            return this._loop
        },
        setLooping: function (a) {
            this._loop = a;
            for (var b in this.tracks) {
                this.tracks[b].loop = a
            }
        },
        getVolume: function () {
            return this._volume
        },
        setVolume: function (a) {
            this._volume = a.limit(0, 1);
            for (var b in this.tracks) {
                this.tracks[b].volume = this._volume
            }
        },
        fadeOut: function (a) {
            if (!this.currentTrack) {
                return
            }
            clearInterval(this._fadeInterval);
            this.fadeTimer = new ig.Timer(a);
            this._fadeInterval = setInterval(this._fadeStep.bind(this), 50)
        },
        _fadeStep: function () {
            var a = this.fadeTimer.delta().map(-this.fadeTimer.target, 0, 1, 0).limit(0, 1) * this._volume;
            if (a <= 0.01) {
                this.stop();
                this.currentTrack.volume = this._volume;
                clearInterval(this._fadeInterval)
            } else {
                this.currentTrack.volume = a
            }
        },
        _endedCallback: function () {
            if (this._loop) {
                this.play()
            } else {
                this.next()
            }
        }
    });
    ig.Sound = ig.Class.extend({
        path: "",
        volume: 1,
        currentClip: null,
        multiChannel: true,
        _loop: false,
        init: function (b, a) {
            this.path = b;
            this.multiChannel = (a !== false);
            Object.defineProperty(this, "loop", {get: this.getLooping.bind(this), set: this.setLooping.bind(this)});
            this.load()
        },
        getLooping: function () {
            return this._loop
        },
        setLooping: function (a) {
            this._loop = a;
            if (this.currentClip) {
                this.currentClip.loop = a
            }
        },
        load: function (a) {
            if (!ig.Sound.enabled) {
                if (a) {
                    a(this.path, true)
                }
                return
            }
            if (ig.ready) {
                ig.soundManager.load(this.path, this.multiChannel, a)
            } else {
                ig.addResource(this)
            }
        },
        play: function () {
            if (!ig.Sound.enabled) {
                return
            }
            this.currentClip = ig.soundManager.get(this.path);
            this.currentClip.loop = this._loop;
            this.currentClip.volume = ig.soundManager.volume * this.volume;
            this.currentClip.play()
        },
        stop: function () {
            if (this.currentClip) {
                this.currentClip.pause();
                this.currentClip.currentTime = 0
            }
        }
    });
    ig.Sound.WebAudioSource = ig.Class.extend({
        sources: [], gain: null, buffer: null, _loop: false, init: function () {
            this.gain = ig.soundManager.audioContext.createGain();
            this.gain.connect(ig.soundManager.audioContext.destination);
            Object.defineProperty(this, "loop", {get: this.getLooping.bind(this), set: this.setLooping.bind(this)});
            Object.defineProperty(this, "volume", {get: this.getVolume.bind(this), set: this.setVolume.bind(this)})
        }, play: function () {
            if (!this.buffer) {
                return
            }
            var b = ig.soundManager.audioContext.createBufferSource();
            b.buffer = this.buffer;
            b.connect(this.gain);
            b.loop = this._loop;
            var a = this;
            this.sources.push(b);
            b.onended = function () {
                a.sources.erase(b)
            };
            b.start(0)
        }, pause: function () {
            for (var a = 0; a < this.sources.length; a++) {
                try {
                    this.sources[a].stop()
                } catch (b) {
                }
            }
        }, getLooping: function () {
            return this._loop
        }, setLooping: function (a) {
            this._loop = a;
            for (var b = 0; b < this.sources.length; b++) {
                this.sources[b].loop = a
            }
        }, getVolume: function () {
            return this.gain.gain.value
        }, setVolume: function (a) {
            this.gain.gain.value = a
        }
    });
    ig.Sound.FORMAT = {
        MP3: {ext: "mp3", mime: "audio/mpeg"},
        M4A: {ext: "m4a", mime: "audio/mp4; codecs=mp4a"},
        OGG: {ext: "ogg", mime: "audio/ogg; codecs=vorbis"},
        WEBM: {ext: "webm", mime: "audio/webm; codecs=vorbis"},
        CAF: {ext: "caf", mime: "audio/x-caf"}
    };
    ig.Sound.use = [ig.Sound.FORMAT.OGG, ig.Sound.FORMAT.MP3];
    ig.Sound.channels = 4;
    ig.Sound.enabled = true;
    ig.normalizeVendorAttribute(window, "AudioContext");
    ig.Sound.useWebAudio = !!window.AudioContext && !window.nwf
});
ig.baked = true;
ig.module("impact.loader").requires("impact.image", "impact.font", "impact.sound").defines(function () {
    ig.Loader = ig.Class.extend({
        resources: [],
        gameClass: null,
        status: 0,
        done: false,
        _unloaded: [],
        _drawStatus: 0,
        _intervalId: 0,
        _loadCallbackBound: null,
        init: function (b, c) {
            this.gameClass = b;
            this.resources = c;
            this._loadCallbackBound = this._loadCallback.bind(this);
            for (var a = 0; a < this.resources.length; a++) {
                this._unloaded.push(this.resources[a].path)
            }
        },
        load: function () {
            ig.system.clear("#000");
            if (!this.resources.length) {
                this.end();
                return
            }
            for (var a = 0; a < this.resources.length; a++) {
                this.loadResource(this.resources[a])
            }
            this._intervalId = setInterval(this.draw.bind(this), 16)
        },
        loadResource: function (a) {
            a.load(this._loadCallbackBound)
        },
        end: function () {
            if (this.done) {
                return
            }
            this.done = true;
            clearInterval(this._intervalId);
            ig.system.setGame(this.gameClass)
        },
        draw: function () {
            this._drawStatus += (this.status - this._drawStatus) / 5;
            var d = ig.system.scale;
            var b = ig.system.width * 0.6;
            var c = ig.system.height * 0.1;
            var a = ig.system.width * 0.5 - b / 2;
            var e = ig.system.height * 0.5 - c / 2;
            ig.system.context.fillStyle = "#000";
            ig.system.context.fillRect(0, 0, 480, 320);
            ig.system.context.fillStyle = "#fff";
            ig.system.context.fillRect(a * d, e * d, b * d, c * d);
            ig.system.context.fillStyle = "#000";
            ig.system.context.fillRect(a * d + d, e * d + d, b * d - d - d, c * d - d - d);
            ig.system.context.fillStyle = "#fff";
            ig.system.context.fillRect(a * d, e * d, b * d * this._drawStatus, c * d)
        },
        _loadCallback: function (b, a) {
            if (a) {
                this._unloaded.erase(b)
            } else {
                throw ("Failed to load resource: " + b)
            }
            this.status = 1 - (this._unloaded.length / this.resources.length);
            if (this._unloaded.length == 0) {
                setTimeout(this.end.bind(this), 250)
            }
        }
    })
});
ig.baked = true;
ig.module("impact.timer").defines(function () {
    ig.Timer = ig.Class.extend({
        target: 0, base: 0, last: 0, pausedAt: 0, init: function (a) {
            this.base = ig.Timer.time;
            this.last = ig.Timer.time;
            this.target = a || 0
        }, set: function (a) {
            this.target = a || 0;
            this.base = ig.Timer.time;
            this.pausedAt = 0
        }, reset: function () {
            this.base = ig.Timer.time;
            this.pausedAt = 0
        }, tick: function () {
            var a = ig.Timer.time - this.last;
            this.last = ig.Timer.time;
            return (this.pausedAt ? 0 : a)
        }, delta: function () {
            return (this.pausedAt || ig.Timer.time) - this.base - this.target
        }, pause: function () {
            if (!this.pausedAt) {
                this.pausedAt = ig.Timer.time
            }
        }, unpause: function () {
            if (this.pausedAt) {
                this.base += ig.Timer.time - this.pausedAt;
                this.pausedAt = 0
            }
        }
    });
    ig.Timer._last = 0;
    ig.Timer.time = Number.MIN_VALUE;
    ig.Timer.timeScale = 1;
    ig.Timer.maxStep = 0.05;
    ig.Timer.step = function () {
        var a = Date.now();
        var b = (a - ig.Timer._last) / 1000;
        ig.Timer.time += Math.min(b, ig.Timer.maxStep) * ig.Timer.timeScale;
        ig.Timer._last = a
    }
});
ig.baked = true;
ig.module("impact.system").requires("impact.timer", "impact.image").defines(function () {
    ig.System = ig.Class.extend({
        fps: 30,
        width: 320,
        height: 240,
        realWidth: 320,
        realHeight: 240,
        scale: 1,
        tick: 0,
        animationId: 0,
        newGameClass: null,
        running: false,
        delegate: null,
        clock: null,
        canvas: null,
        context: null,
        init: function (e, c, b, a, d) {
            this.fps = c;
            this.clock = new ig.Timer();
            this.canvas = ig.$(e);
            this.resize(b, a, d);
            this.context = this.canvas.getContext("2d");
            this.getDrawPos = ig.System.drawMode;
            if (this.scale != 1) {
                ig.System.scaleMode = ig.System.SCALE.CRISP
            }
            ig.System.scaleMode(this.canvas, this.context)
        },
        resize: function (b, a, c) {
            this.width = b;
            this.height = a;
            this.scale = c || this.scale;
            this.realWidth = this.width * this.scale;
            this.realHeight = this.height * this.scale;
            this.canvas.width = this.realWidth;
            this.canvas.height = this.realHeight
        },
        setGame: function (a) {
            if (this.running) {
                this.newGameClass = a
            } else {
                this.setGameNow(a)
            }
        },
        setGameNow: function (a) {
            ig.game = new (a)();
            ig.system.setDelegate(ig.game)
        },
        setDelegate: function (a) {
            if (typeof(a.run) == "function") {
                this.delegate = a;
                this.startRunLoop()
            } else {
                throw ("System.setDelegate: No run() function in object")
            }
        },
        stopRunLoop: function () {
            ig.clearAnimation(this.animationId);
            this.running = false
        },
        startRunLoop: function () {
            this.stopRunLoop();
            this.animationId = ig.setAnimation(this.run.bind(this), this.canvas);
            this.running = true
        },
        clear: function (a) {
            this.context.fillStyle = a;
            this.context.fillRect(0, 0, this.realWidth, this.realHeight)
        },
        run: function () {
            ig.Timer.step();
            this.tick = this.clock.tick();
            this.delegate.run();
            ig.input.clearPressed();
            if (this.newGameClass) {
                this.setGameNow(this.newGameClass);
                this.newGameClass = null
            }
        },
        getDrawPos: null
    });
    ig.System.DRAW = {
        AUTHENTIC: function (a) {
            return Math.round(a) * this.scale
        }, SMOOTH: function (a) {
            return Math.round(a * this.scale)
        }, SUBPIXEL: function (a) {
            return a * this.scale
        }
    };
    ig.System.drawMode = ig.System.DRAW.SMOOTH;
    ig.System.SCALE = {
        CRISP: function (a, b) {
            ig.setVendorAttribute(b, "imageSmoothingEnabled", false);
            a.style.imageRendering = "-moz-crisp-edges";
            a.style.imageRendering = "-o-crisp-edges";
            a.style.imageRendering = "-webkit-optimize-contrast";
            a.style.imageRendering = "crisp-edges";
            a.style.msInterpolationMode = "nearest-neighbor"
        }, SMOOTH: function (a, b) {
            ig.setVendorAttribute(b, "imageSmoothingEnabled", true);
            a.style.imageRendering = "";
            a.style.msInterpolationMode = ""
        }
    };
    ig.System.scaleMode = ig.System.SCALE.SMOOTH
});
ig.baked = true;
ig.module("impact.input").defines(function () {
    ig.KEY = {
        "MOUSE1": -1,
        "MOUSE2": -3,
        "MWHEEL_UP": -4,
        "MWHEEL_DOWN": -5,
        "BACKSPACE": 8,
        "TAB": 9,
        "ENTER": 13,
        "PAUSE": 19,
        "CAPS": 20,
        "ESC": 27,
        "SPACE": 32,
        "PAGE_UP": 33,
        "PAGE_DOWN": 34,
        "END": 35,
        "HOME": 36,
        "LEFT_ARROW": 37,
        "UP_ARROW": 38,
        "RIGHT_ARROW": 39,
        "DOWN_ARROW": 40,
        "INSERT": 45,
        "DELETE": 46,
        "_0": 48,
        "_1": 49,
        "_2": 50,
        "_3": 51,
        "_4": 52,
        "_5": 53,
        "_6": 54,
        "_7": 55,
        "_8": 56,
        "_9": 57,
        "A": 65,
        "B": 66,
        "C": 67,
        "D": 68,
        "E": 69,
        "F": 70,
        "G": 71,
        "H": 72,
        "I": 73,
        "J": 74,
        "K": 75,
        "L": 76,
        "M": 77,
        "N": 78,
        "O": 79,
        "P": 80,
        "Q": 81,
        "R": 82,
        "S": 83,
        "T": 84,
        "U": 85,
        "V": 86,
        "W": 87,
        "X": 88,
        "Y": 89,
        "Z": 90,
        "NUMPAD_0": 96,
        "NUMPAD_1": 97,
        "NUMPAD_2": 98,
        "NUMPAD_3": 99,
        "NUMPAD_4": 100,
        "NUMPAD_5": 101,
        "NUMPAD_6": 102,
        "NUMPAD_7": 103,
        "NUMPAD_8": 104,
        "NUMPAD_9": 105,
        "MULTIPLY": 106,
        "ADD": 107,
        "SUBSTRACT": 109,
        "DECIMAL": 110,
        "DIVIDE": 111,
        "F1": 112,
        "F2": 113,
        "F3": 114,
        "F4": 115,
        "F5": 116,
        "F6": 117,
        "F7": 118,
        "F8": 119,
        "F9": 120,
        "F10": 121,
        "F11": 122,
        "F12": 123,
        "SHIFT": 16,
        "CTRL": 17,
        "ALT": 18,
        "PLUS": 187,
        "COMMA": 188,
        "MINUS": 189,
        "PERIOD": 190
    };
    ig.Input = ig.Class.extend({
        bindings: {},
        actions: {},
        presses: {},
        locks: {},
        delayedKeyup: {},
        isUsingMouse: false,
        isUsingKeyboard: false,
        isUsingAccelerometer: false,
        mouse: {x: 0, y: 0},
        accel: {x: 0, y: 0, z: 0},
        initMouse: function () {
            if (this.isUsingMouse) {
                return
            }
            this.isUsingMouse = true;
            var a = this.mousewheel.bind(this);
            ig.system.canvas.addEventListener("mousewheel", a, false);
            ig.system.canvas.addEventListener("DOMMouseScroll", a, false);
            ig.system.canvas.addEventListener("contextmenu", this.contextmenu.bind(this), false);
            ig.system.canvas.addEventListener("mousedown", this.keydown.bind(this), false);
            ig.system.canvas.addEventListener("mouseup", this.keyup.bind(this), false);
            ig.system.canvas.addEventListener("mousemove", this.mousemove.bind(this), false);
            if (ig.ua.touchDevice) {
                ig.system.canvas.addEventListener("touchstart", this.keydown.bind(this), false);
                ig.system.canvas.addEventListener("touchend", this.keyup.bind(this), false);
                ig.system.canvas.addEventListener("touchmove", this.mousemove.bind(this), false);
                ig.system.canvas.addEventListener("MSPointerDown", this.keydown.bind(this), false);
                ig.system.canvas.addEventListener("MSPointerUp", this.keyup.bind(this), false);
                ig.system.canvas.addEventListener("MSPointerMove", this.mousemove.bind(this), false);
                ig.system.canvas.style.msTouchAction = "none"
            }
        },
        initKeyboard: function () {
            if (this.isUsingKeyboard) {
                return
            }
            this.isUsingKeyboard = true;
            window.addEventListener("keydown", this.keydown.bind(this), false);
            window.addEventListener("keyup", this.keyup.bind(this), false)
        },
        initAccelerometer: function () {
            if (this.isUsingAccelerometer) {
                return
            }
            this.isUsingAccelerometer = true;
            window.addEventListener("devicemotion", this.devicemotion.bind(this), false)
        },
        mousewheel: function (b) {
            var d = b.wheelDelta ? b.wheelDelta : (b.detail * -1);
            var a = d > 0 ? ig.KEY.MWHEEL_UP : ig.KEY.MWHEEL_DOWN;
            var c = this.bindings[a];
            if (c) {
                this.actions[c] = true;
                this.presses[c] = true;
                this.delayedKeyup[c] = true;
                b.stopPropagation();
                b.preventDefault()
            }
        },
        mousemove: function (c) {
            var a = parseInt(ig.system.canvas.offsetWidth) || ig.system.realWidth;
            var d = ig.system.scale * (a / ig.system.realWidth);
            var e = {left: 0, top: 0};
            if (ig.system.canvas.getBoundingClientRect) {
                e = ig.system.canvas.getBoundingClientRect()
            }
            var b = c.touches ? c.touches[0] : c;
            this.mouse.x = (b.clientX - e.left) / d;
            this.mouse.y = (b.clientY - e.top) / d
        },
        contextmenu: function (a) {
            if (this.bindings[ig.KEY.MOUSE2]) {
                a.stopPropagation();
                a.preventDefault()
            }
        },
        keydown: function (c) {
            var a = c.target.tagName;
            if (a == "INPUT" || a == "TEXTAREA") {
                return
            }
            var b = c.type == "keydown" ? c.keyCode : (c.button == 2 ? ig.KEY.MOUSE2 : ig.KEY.MOUSE1);
            if (b < 0 && !ig.ua.mobile) {
                window.focus()
            }
            if (c.type == "touchstart" || c.type == "mousedown") {
                this.mousemove(c)
            }
            var d = this.bindings[b];
            if (d) {
                this.actions[d] = true;
                if (!this.locks[d]) {
                    this.presses[d] = true;
                    this.locks[d] = true
                }
                c.preventDefault()
            }
        },
        keyup: function (c) {
            var a = c.target.tagName;
            if (a == "INPUT" || a == "TEXTAREA") {
                return
            }
            var b = c.type == "keyup" ? c.keyCode : (c.button == 2 ? ig.KEY.MOUSE2 : ig.KEY.MOUSE1);
            var d = this.bindings[b];
            if (d) {
                this.delayedKeyup[d] = true;
                c.preventDefault()
            }
        },
        devicemotion: function (a) {
            this.accel = a.accelerationIncludingGravity
        },
        bind: function (a, b) {
            if (a < 0) {
                this.initMouse()
            } else {
                if (a > 0) {
                    this.initKeyboard()
                }
            }
            this.bindings[a] = b
        },
        bindTouch: function (a, d) {
            var b = ig.$(a);
            var c = this;
            b.addEventListener("touchstart", function (e) {
                c.touchStart(e, d)
            }, false);
            b.addEventListener("touchend", function (e) {
                c.touchEnd(e, d)
            }, false);
            b.addEventListener("MSPointerDown", function (e) {
                c.touchStart(e, d)
            }, false);
            b.addEventListener("MSPointerUp", function (e) {
                c.touchEnd(e, d)
            }, false)
        },
        unbind: function (a) {
            var b = this.bindings[a];
            this.delayedKeyup[b] = true;
            this.bindings[a] = null
        },
        unbindAll: function () {
            this.bindings = {};
            this.actions = {};
            this.presses = {};
            this.locks = {};
            this.delayedKeyup = {}
        },
        state: function (a) {
            return this.actions[a]
        },
        pressed: function (a) {
            return this.presses[a]
        },
        released: function (a) {
            return !!this.delayedKeyup[a]
        },
        clearPressed: function () {
            for (var a in this.delayedKeyup) {
                this.actions[a] = false;
                this.locks[a] = false
            }
            this.delayedKeyup = {};
            this.presses = {}
        },
        touchStart: function (a, b) {
            this.actions[b] = true;
            this.presses[b] = true;
            a.stopPropagation();
            a.preventDefault();
            return false
        },
        touchEnd: function (a, b) {
            this.delayedKeyup[b] = true;
            a.stopPropagation();
            a.preventDefault();
            return false
        }
    })
});
ig.baked = true;
ig.module("impact.impact").requires("dom.ready", "impact.loader", "impact.system", "impact.input", "impact.sound").defines(function () {
    ig.main = function (h, e, f, d, b, g, c) {
        ig.system = new ig.System(h, f, d, b, g || 1);
        ig.input = new ig.Input();
        ig.soundManager = new ig.SoundManager();
        ig.music = new ig.Music();
        ig.ready = true;
        var a = new (c || ig.Loader)(e, ig.resources);
        a.load()
    }
});
var TankGame = {};
var codeDoucCount;
TankGame.apply = new (function () {
    this.events = {};
    this.bind = function (a, b) {
        if (typeof b == "function") {
            this.events[a] = b
        }
    };
    this.unbind = function (a) {
        if (this.events[a]) {
            this.events[a] = null
        }
    };
    this.trigger = function (a, b) {
        if (this.events[a]) {
            this.events[a].call(b || ig.game)
        }
    }
});
TankGame.LevelData = {};
TankGame.config = new (function () {
    this.addDifficulty = function (b, a) {
        if (TankGame.LevelData[b]) {
            var c = TankGame.LevelData[b];
            for (var d in c) {
                if (a[d]) {
                    c[d] = a[d]
                }
            }
        } else {
            TankGame.LevelData[b] = a
        }
        if (a.isDefault) {
            this.defaultDifficulty = b
        }
        if (this.defaultDifficulty == "") {
            this.defaultDifficulty = b
        }
    };
    this.defaultDifficulty = ""
})();
TankGame.params = new (function () {
    var a = {};
    a.addParam = function (b, c) {
        this[b] = c
    };
    a.getUrlParams = function (c) {
        var b = {openId: this.openId, gameNo: this.gameNo, from: this.from};
        c = ig.merge(b, c);
        return c
    };
    return a
})();
TankGame.isGameTime = true;
TankGame.isAddCheckEvent = false;
var shareTitle = "招商银行—9分坦克战";
var totalnum = 0;
TankGame.gameInfo = new (function () {
    this.number = 0;
    this.score = 0;
    this.total = 0;
    this.percent = 0;
    this.gameStartTime = 0;
    this.gameTime = 0;
    this.payScore = 9;
    this.canNotPayTip = "亲，您的积分不足，还是再赚些积分再来吧~！";
    this.runSpeed = 150;
    this.maxScore = 9;
    this.totalScore = 0;
    this.shootNumber = 0;
    this.setTotal = function (a) {
        this.total = a
    };
    this.setPercent = function (a) {
        this.percent = a
    };
    this.setTotalScore = function (a) {
        this.totalScore = a
    };
    this.gameOver = function (a) {
        this.gameTime = a - this.gameStartTime
    };
    this.addNumber = function () {
        this.number++;
        if (this.number % 3 == 0) {
            TankGame.difficulty += 2
        }
    };
    this.setPayScore = function (a) {
        if (a > 0) {
            this.payScore = a
        }
    };
    this.payScored = function () {
        this.totalScore -= this.payScore
    };
    this.addScore = function (a) {
        this.totalScore += a
    };
    this.setMaxScore = function (a) {
        this.maxScore = a
    };
    this.fetchScore = function () {
        return Math.min(this.number, this.maxScore)
    };
    this.isPayEnabled = function () {
        return this.totalScore > this.payScore
    };
    this.setShootNumber = function (a) {
        this.shootNumber = a
    };
    this.reset = function () {
        this.number = 0;
        this.gameStartTime = ig.system.clock.last
    }
});
ig.baked = true;
ig.module("impact.animation").requires("impact.timer", "impact.image").defines(function () {
    ig.AnimationSheet = ig.Class.extend({
        width: 8, height: 8, image: null, init: function (c, b, a) {
            this.width = b;
            this.height = a;
            this.image = new ig.Image(c)
        }
    });
    ig.Animation = ig.Class.extend({
        sheet: null,
        timer: null,
        sequence: [],
        flip: {x: false, y: false},
        pivot: {x: 0, y: 0},
        frame: 0,
        tile: 0,
        loopCount: 0,
        alpha: 1,
        angle: 0,
        init: function (b, c, d, a) {
            this.sheet = b;
            this.pivot = {x: b.width / 2, y: b.height / 2};
            this.timer = new ig.Timer();
            this.frameTime = c;
            this.sequence = d;
            this.stop = !!a;
            this.tile = this.sequence[0]
        },
        rewind: function () {
            this.timer.set();
            this.loopCount = 0;
            this.frame = 0;
            this.tile = this.sequence[0];
            return this
        },
        gotoFrame: function (a) {
            this.timer.set(this.frameTime * -a - 0.0001);
            this.update()
        },
        gotoRandomFrame: function () {
            this.gotoFrame(Math.floor(Math.random() * this.sequence.length))
        },
        update: function () {
            var a = Math.floor(this.timer.delta() / this.frameTime);
            this.loopCount = Math.floor(a / this.sequence.length);
            if (this.stop && this.loopCount > 0) {
                this.frame = this.sequence.length - 1
            } else {
                this.frame = a % this.sequence.length
            }
            this.tile = this.sequence[this.frame]
        },
        draw: function (c, b) {
            var a = Math.max(this.sheet.width, this.sheet.height);
            if (c > ig.system.width || b > ig.system.height || c + a < 0 || b + a < 0) {
                return
            }
            if (this.alpha != 1) {
                ig.system.context.globalAlpha = this.alpha
            }
            if (this.angle == 0) {
                this.sheet.image.drawTile(c, b, this.tile, this.sheet.width, this.sheet.height, this.flip.x, this.flip.y)
            } else {
                ig.system.context.save();
                ig.system.context.translate(ig.system.getDrawPos(c + this.pivot.x), ig.system.getDrawPos(b + this.pivot.y));
                ig.system.context.rotate(this.angle);
                this.sheet.image.drawTile(-this.pivot.x, -this.pivot.y, this.tile, this.sheet.width, this.sheet.height, this.flip.x, this.flip.y);
                ig.system.context.restore()
            }
            if (this.alpha != 1) {
                ig.system.context.globalAlpha = 1
            }
        }
    })
});
ig.baked = true;
ig.module("impact.entity").requires("impact.animation", "impact.impact").defines(function () {
    ig.Entity = ig.Class.extend({
        id: 0,
        settings: {},
        size: {x: 16, y: 16},
        offset: {x: 0, y: 0},
        pos: {x: 0, y: 0},
        last: {x: 0, y: 0},
        vel: {x: 0, y: 0},
        accel: {x: 0, y: 0},
        friction: {x: 0, y: 0},
        maxVel: {x: 100, y: 100},
        zIndex: 0,
        gravityFactor: 1,
        standing: false,
        bounciness: 0,
        minBounceVelocity: 40,
        anims: {},
        animSheet: null,
        currentAnim: null,
        health: 10,
        type: 0,
        checkAgainst: 0,
        collides: 0,
        _killed: false,
        slopeStanding: {min: (44).toRad(), max: (136).toRad()},
        init: function (a, c, b) {
            this.id = ++ig.Entity._lastId;
            this.pos.x = this.last.x = a;
            this.pos.y = this.last.y = c;
            ig.merge(this, b)
        },
        reset: function (a, d, b) {
            var c = this.constructor.prototype;
            this.pos.x = a;
            this.pos.y = d;
            this.last.x = a;
            this.last.y = d;
            this.vel.x = c.vel.x;
            this.vel.y = c.vel.y;
            this.accel.x = c.accel.x;
            this.accel.y = c.accel.y;
            this.health = c.health;
            this._killed = c._killed;
            this.standing = c.standing;
            this.type = c.type;
            this.checkAgainst = c.checkAgainst;
            this.collides = c.collides;
            ig.merge(this, b)
        },
        addAnim: function (c, e, f, d) {
            if (!this.animSheet) {
                throw ("No animSheet to add the animation " + c + " to.")
            }
            var b = new ig.Animation(this.animSheet, e, f, d);
            this.anims[c] = b;
            if (!this.currentAnim) {
                this.currentAnim = b
            }
            return b
        },
        update: function () {
            this.last.x = this.pos.x;
            this.last.y = this.pos.y;
            this.vel.y += ig.game.gravity * ig.system.tick * this.gravityFactor;
            this.vel.x = this.getNewVelocity(this.vel.x, this.accel.x, this.friction.x, this.maxVel.x);
            this.vel.y = this.getNewVelocity(this.vel.y, this.accel.y, this.friction.y, this.maxVel.y);
            var c = this.vel.x * ig.system.tick;
            var b = this.vel.y * ig.system.tick;
            var a = ig.game.collisionMap.trace(this.pos.x, this.pos.y, c, b, this.size.x, this.size.y);
            this.handleMovementTrace(a);
            if (this.currentAnim) {
                this.currentAnim.update()
            }
        },
        getNewVelocity: function (c, b, d, a) {
            if (b) {
                return (c + b * ig.system.tick).limit(-a, a)
            } else {
                if (d) {
                    var e = d * ig.system.tick;
                    if (c - e > 0) {
                        return c - e
                    } else {
                        if (c + e < 0) {
                            return c + e
                        } else {
                            return 0
                        }
                    }
                }
            }
            return c.limit(-a, a)
        },
        handleMovementTrace: function (c) {
            this.standing = false;
            if (c.collision.y) {
                if (this.bounciness > 0 && Math.abs(this.vel.y) > this.minBounceVelocity) {
                    this.vel.y *= -this.bounciness
                } else {
                    if (this.vel.y > 0) {
                        this.standing = true
                    }
                    this.vel.y = 0
                }
            }
            if (c.collision.x) {
                if (this.bounciness > 0 && Math.abs(this.vel.x) > this.minBounceVelocity) {
                    this.vel.x *= -this.bounciness
                } else {
                    this.vel.x = 0
                }
            }
            if (c.collision.slope) {
                var e = c.collision.slope;
                if (this.bounciness > 0) {
                    var d = this.vel.x * e.nx + this.vel.y * e.ny;
                    this.vel.x = (this.vel.x - e.nx * d * 2) * this.bounciness;
                    this.vel.y = (this.vel.y - e.ny * d * 2) * this.bounciness
                } else {
                    var b = e.x * e.x + e.y * e.y;
                    var a = (this.vel.x * e.x + this.vel.y * e.y) / b;
                    this.vel.x = e.x * a;
                    this.vel.y = e.y * a;
                    var f = Math.atan2(e.x, e.y);
                    if (f > this.slopeStanding.min && f < this.slopeStanding.max) {
                        this.standing = true
                    }
                }
            }
            this.pos = c.pos
        },
        draw: function () {
            if (this.currentAnim) {
                this.currentAnim.draw(this.pos.x - this.offset.x - ig.game._rscreen.x, this.pos.y - this.offset.y - ig.game._rscreen.y)
            }
        },
        kill: function () {
            ig.game.removeEntity(this)
        },
        receiveDamage: function (a, b) {
            this.health -= a;
            if (this.health <= 0) {
                this.kill()
            }
        },
        touches: function (a) {
            return !(this.pos.x >= a.pos.x + a.size.x || this.pos.x + this.size.x <= a.pos.x || this.pos.y >= a.pos.y + a.size.y || this.pos.y + this.size.y <= a.pos.y)
        },
        distanceTo: function (a) {
            var b = (this.pos.x + this.size.x / 2) - (a.pos.x + a.size.x / 2);
            var c = (this.pos.y + this.size.y / 2) - (a.pos.y + a.size.y / 2);
            return Math.sqrt(b * b + c * c)
        },
        angleTo: function (a) {
            return Math.atan2((a.pos.y + a.size.y / 2) - (this.pos.y + this.size.y / 2), (a.pos.x + a.size.x / 2) - (this.pos.x + this.size.x / 2))
        },
        check: function (a) {
        },
        collideWith: function (a, b) {
        },
        ready: function () {
        },
        erase: function () {
        }
    });
    ig.Entity._lastId = 0;
    ig.Entity.COLLIDES = {NEVER: 0, LITE: 1, PASSIVE: 2, ACTIVE: 4, FIXED: 8};
    ig.Entity.TYPE = {NONE: 0, A: 1, B: 2, BOTH: 3};
    ig.Entity.checkPair = function (d, c) {
        if (d.checkAgainst & c.type) {
            d.check(c)
        }
        if (c.checkAgainst & d.type) {
            c.check(d)
        }
        if (d.collides && c.collides && d.collides + c.collides > ig.Entity.COLLIDES.ACTIVE) {
            ig.Entity.solveCollision(d, c)
        }
    };
    ig.Entity.solveCollision = function (d, c) {
        var e = null;
        if (d.collides == ig.Entity.COLLIDES.LITE || c.collides == ig.Entity.COLLIDES.FIXED) {
            e = d
        } else {
            if (c.collides == ig.Entity.COLLIDES.LITE || d.collides == ig.Entity.COLLIDES.FIXED) {
                e = c
            }
        }
        if (d.last.x + d.size.x > c.last.x && d.last.x < c.last.x + c.size.x) {
            if (d.last.y < c.last.y) {
                ig.Entity.seperateOnYAxis(d, c, e)
            } else {
                ig.Entity.seperateOnYAxis(c, d, e)
            }
            d.collideWith(c, "y");
            c.collideWith(d, "y")
        } else {
            if (d.last.y + d.size.y > c.last.y && d.last.y < c.last.y + c.size.y) {
                if (d.last.x < c.last.x) {
                    ig.Entity.seperateOnXAxis(d, c, e)
                } else {
                    ig.Entity.seperateOnXAxis(c, d, e)
                }
                d.collideWith(c, "x");
                c.collideWith(d, "x")
            }
        }
    };
    ig.Entity.seperateOnXAxis = function (b, i, e) {
        var d = (b.pos.x + b.size.x - i.pos.x);
        if (e) {
            var h = b === e ? i : b;
            e.vel.x = -e.vel.x * e.bounciness + h.vel.x;
            var f = ig.game.collisionMap.trace(e.pos.x, e.pos.y, e == b ? -d : d, 0, e.size.x, e.size.y);
            e.pos.x = f.pos.x
        } else {
            var g = (b.vel.x - i.vel.x) / 2;
            b.vel.x = -g;
            i.vel.x = g;
            var c = ig.game.collisionMap.trace(b.pos.x, b.pos.y, -d / 2, 0, b.size.x, b.size.y);
            b.pos.x = Math.floor(c.pos.x);
            var a = ig.game.collisionMap.trace(i.pos.x, i.pos.y, d / 2, 0, i.size.x, i.size.y);
            i.pos.x = Math.ceil(a.pos.x)
        }
    };
    ig.Entity.seperateOnYAxis = function (f, a, c) {
        var b = (f.pos.y + f.size.y - a.pos.y);
        if (c) {
            var h = f === c ? a : f;
            c.vel.y = -c.vel.y * c.bounciness + h.vel.y;
            var i = 0;
            if (c == f && Math.abs(c.vel.y - h.vel.y) < c.minBounceVelocity) {
                c.standing = true;
                i = h.vel.x * ig.system.tick
            }
            var d = ig.game.collisionMap.trace(c.pos.x, c.pos.y, i, c == f ? -b : b, c.size.x, c.size.y);
            c.pos.y = d.pos.y;
            c.pos.x = d.pos.x
        } else {
            if (ig.game.gravity && (a.standing || f.vel.y > 0)) {
                var j = ig.game.collisionMap.trace(f.pos.x, f.pos.y, 0, -(f.pos.y + f.size.y - a.pos.y), f.size.x, f.size.y);
                f.pos.y = j.pos.y;
                if (f.bounciness > 0 && f.vel.y > f.minBounceVelocity) {
                    f.vel.y *= -f.bounciness
                } else {
                    f.standing = true;
                    f.vel.y = 0
                }
            } else {
                var g = (f.vel.y - a.vel.y) / 2;
                f.vel.y = -g;
                a.vel.y = g;
                var i = a.vel.x * ig.system.tick;
                var j = ig.game.collisionMap.trace(f.pos.x, f.pos.y, i, -b / 2, f.size.x, f.size.y);
                f.pos.y = j.pos.y;
                var e = ig.game.collisionMap.trace(a.pos.x, a.pos.y, 0, b / 2, a.size.x, a.size.y);
                a.pos.y = e.pos.y
            }
        }
    }
});
ig.baked = true;
ig.module("impact.map").defines(function () {
    ig.Map = ig.Class.extend({
        tilesize: 8, width: 1, height: 1, data: [[]], name: null, init: function (b, a) {
            this.tilesize = b;
            this.data = a;
            this.height = a.length;
            this.width = a[0].length;
            this.pxWidth = this.width * this.tilesize;
            this.pxHeight = this.height * this.tilesize
        }, getTile: function (b, d) {
            var c = Math.floor(b / this.tilesize);
            var a = Math.floor(d / this.tilesize);
            if ((c >= 0 && c < this.width) && (a >= 0 && a < this.height)) {
                return this.data[a][c]
            } else {
                return 0
            }
        }, setTile: function (b, e, d) {
            var c = Math.floor(b / this.tilesize);
            var a = Math.floor(e / this.tilesize);
            if ((c >= 0 && c < this.width) && (a >= 0 && a < this.height)) {
                this.data[a][c] = d
            }
        }
    })
});
ig.baked = true;
ig.module("impact.collision-map").requires("impact.map").defines(function () {
    ig.CollisionMap = ig.Map.extend({
        lastSlope: 1, tiledef: null, init: function (i, h, g) {
            this.parent(i, h);
            this.tiledef = g || ig.CollisionMap.defaultTileDef;
            for (var f in this.tiledef) {
                if (f | 0 > this.lastSlope) {
                    this.lastSlope = f | 0
                }
            }
        }, trace: function (q, m, n, k, g, f) {
            var j = {collision: {x: false, y: false, slope: false}, pos: {x: q, y: m}, tile: {x: 0, y: 0}};
            var o = Math.ceil(Math.max(Math.abs(n), Math.abs(k)) / this.tilesize);
            if (o > 1) {
                var p = n / o;
                var l = k / o;
                for (var h = 0; h < o && (p || l); h++) {
                    this._traceStep(j, q, m, p, l, g, f, n, k, h);
                    q = j.pos.x;
                    m = j.pos.y;
                    if (j.collision.x) {
                        p = 0;
                        n = 0
                    }
                    if (j.collision.y) {
                        l = 0;
                        k = 0
                    }
                    if (j.collision.slope) {
                        break
                    }
                }
            } else {
                this._traceStep(j, q, m, n, k, g, f, n, k, 0)
            }
            return j
        }, _traceStep: function (E, l, i, u, s, w, r, A, v, h) {
            E.pos.x += u;
            E.pos.y += s;
            var q = 0;
            if (u) {
                var B = (u > 0 ? w : 0);
                var m = (u < 0 ? this.tilesize : 0);
                var f = Math.max(Math.floor(i / this.tilesize), 0);
                var C = Math.min(Math.ceil((i + r) / this.tilesize), this.height);
                var n = Math.floor((E.pos.x + B) / this.tilesize);
                var p = Math.floor((l + B) / this.tilesize);
                if (h > 0 || n == p || p < 0 || p >= this.width) {
                    p = -1
                }
                if (n >= 0 && n < this.width) {
                    for (var k = f; k < C; k++) {
                        if (p != -1) {
                            q = this.data[k][p];
                            if (q > 1 && q <= this.lastSlope && this._checkTileDef(E, q, l, i, A, v, w, r, p, k)) {
                                break
                            }
                        }
                        q = this.data[k][n];
                        if (q == 1 || q > this.lastSlope || (q > 1 && this._checkTileDef(E, q, l, i, A, v, w, r, n, k))) {
                            if (q > 1 && q <= this.lastSlope && E.collision.slope) {
                                break
                            }
                            E.collision.x = true;
                            E.tile.x = q;
                            l = E.pos.x = n * this.tilesize - B + m;
                            A = 0;
                            break
                        }
                    }
                }
            }
            if (s) {
                var z = (s > 0 ? r : 0);
                var j = (s < 0 ? this.tilesize : 0);
                var g = Math.max(Math.floor(E.pos.x / this.tilesize), 0);
                var D = Math.min(Math.ceil((E.pos.x + w) / this.tilesize), this.width);
                var k = Math.floor((E.pos.y + z) / this.tilesize);
                var o = Math.floor((i + z) / this.tilesize);
                if (h > 0 || k == o || o < 0 || o >= this.height) {
                    o = -1
                }
                if (k >= 0 && k < this.height) {
                    for (var n = g; n < D; n++) {
                        if (o != -1) {
                            q = this.data[o][n];
                            if (q > 1 && q <= this.lastSlope && this._checkTileDef(E, q, l, i, A, v, w, r, n, o)) {
                                break
                            }
                        }
                        q = this.data[k][n];
                        if (q == 1 || q > this.lastSlope || (q > 1 && this._checkTileDef(E, q, l, i, A, v, w, r, n, k))) {
                            if (q > 1 && q <= this.lastSlope && E.collision.slope) {
                                break
                            }
                            E.collision.y = true;
                            E.tile.y = q;
                            E.pos.y = k * this.tilesize - z + j;
                            break
                        }
                    }
                }
            }
        }, _checkTileDef: function (F, p, n, l, w, v, z, u, o, m) {
            var q = this.tiledef[p];
            if (!q) {
                return false
            }
            var h = (o + q[0]) * this.tilesize, g = (m + q[1]) * this.tilesize, k = (q[2] - q[0]) * this.tilesize, j = (q[3] - q[1]) * this.tilesize, A = q[4];
            var E = n + w + (j < 0 ? z : 0) - h, D = l + v + (k > 0 ? u : 0) - g;
            if (k * D - j * E > 0) {
                if (w * -j + v * k < 0) {
                    return A
                }
                var f = Math.sqrt(k * k + j * j);
                var C = j / f, B = -k / f;
                var i = E * C + D * B;
                var s = C * i, r = B * i;
                if (s * s + r * r >= w * w + v * v) {
                    return A || (k * (D - v) - j * (E - w) < 0.5)
                }
                F.pos.x = n + w - s;
                F.pos.y = l + v - r;
                F.collision.slope = {x: k, y: j, nx: C, ny: B};
                return true
            }
            return false
        }
    });
    var c = 1 / 2, d = 1 / 3, e = 2 / 3, a = true, b = false;
    ig.CollisionMap.defaultTileDef = {
        5: [0, 1, 1, e, a],
        6: [0, e, 1, d, a],
        7: [0, d, 1, 0, a],
        3: [0, 1, 1, c, a],
        4: [0, c, 1, 0, a],
        2: [0, 1, 1, 0, a],
        10: [c, 1, 1, 0, a],
        21: [0, 1, c, 0, a],
        32: [e, 1, 1, 0, a],
        43: [d, 1, e, 0, a],
        54: [0, 1, d, 0, a],
        27: [0, 0, 1, d, a],
        28: [0, d, 1, e, a],
        29: [0, e, 1, 1, a],
        25: [0, 0, 1, c, a],
        26: [0, c, 1, 1, a],
        24: [0, 0, 1, 1, a],
        11: [0, 0, c, 1, a],
        22: [c, 0, 1, 1, a],
        33: [0, 0, d, 1, a],
        44: [d, 0, e, 1, a],
        55: [e, 0, 1, 1, a],
        16: [1, d, 0, 0, a],
        17: [1, e, 0, d, a],
        18: [1, 1, 0, e, a],
        14: [1, c, 0, 0, a],
        15: [1, 1, 0, c, a],
        13: [1, 1, 0, 0, a],
        8: [c, 1, 0, 0, a],
        19: [1, 1, c, 0, a],
        30: [d, 1, 0, 0, a],
        41: [e, 1, d, 0, a],
        52: [1, 1, e, 0, a],
        38: [1, e, 0, 1, a],
        39: [1, d, 0, e, a],
        40: [1, 0, 0, d, a],
        36: [1, c, 0, 1, a],
        37: [1, 0, 0, c, a],
        35: [1, 0, 0, 1, a],
        9: [1, 0, c, 1, a],
        20: [c, 0, 0, 1, a],
        31: [1, 0, e, 1, a],
        42: [e, 0, d, 1, a],
        53: [d, 0, 0, 1, a],
        12: [0, 0, 1, 0, b],
        23: [1, 1, 0, 1, b],
        34: [1, 0, 1, 1, b],
        45: [0, 1, 0, 0, b]
    };
    ig.CollisionMap.staticNoCollision = {
        trace: function (f, i, h, g) {
            return {collision: {x: false, y: false, slope: false}, pos: {x: f + h, y: i + g}, tile: {x: 0, y: 0}}
        }
    }
});
ig.baked = true;
ig.module("impact.background-map").requires("impact.map", "impact.image").defines(function () {
    ig.BackgroundMap = ig.Map.extend({
        tiles: null,
        scroll: {x: 0, y: 0},
        distance: 1,
        repeat: false,
        tilesetName: "",
        foreground: false,
        enabled: true,
        preRender: false,
        preRenderedChunks: null,
        chunkSize: 512,
        debugChunks: false,
        anims: {},
        init: function (c, a, b) {
            this.parent(c, a);
            this.setTileset(b)
        },
        setTileset: function (a) {
            this.tilesetName = a instanceof ig.Image ? a.path : a;
            this.tiles = new ig.Image(this.tilesetName);
            this.preRenderedChunks = null
        },
        setScreenPos: function (a, b) {
            this.scroll.x = a / this.distance;
            this.scroll.y = b / this.distance
        },
        preRenderMapToChunks: function () {
            var b = this.width * this.tilesize * ig.system.scale, e = this.height * this.tilesize * ig.system.scale;
            this.chunkSize = Math.min(Math.max(b, e), this.chunkSize);
            var d = Math.ceil(b / this.chunkSize), c = Math.ceil(e / this.chunkSize);
            this.preRenderedChunks = [];
            for (var h = 0; h < c; h++) {
                this.preRenderedChunks[h] = [];
                for (var a = 0; a < d; a++) {
                    var f = (a == d - 1) ? b - a * this.chunkSize : this.chunkSize;
                    var g = (h == c - 1) ? e - h * this.chunkSize : this.chunkSize;
                    this.preRenderedChunks[h][a] = this.preRenderChunk(a, h, f, g)
                }
            }
        },
        preRenderChunk: function (c, b, p, g) {
            var i = p / this.tilesize / ig.system.scale + 1, a = g / this.tilesize / ig.system.scale + 1;
            var j = (c * this.chunkSize / ig.system.scale) % this.tilesize, f = (b * this.chunkSize / ig.system.scale) % this.tilesize;
            var e = Math.floor(c * this.chunkSize / this.tilesize / ig.system.scale), d = Math.floor(b * this.chunkSize / this.tilesize / ig.system.scale);
            var o = ig.$new("canvas");
            o.width = p;
            o.height = g;
            o.retinaResolutionEnabled = false;
            var k = o.getContext("2d");
            ig.System.scaleMode(o, k);
            var q = ig.system.context;
            ig.system.context = k;
            for (var n = 0; n < i; n++) {
                for (var m = 0; m < a; m++) {
                    if (n + e < this.width && m + d < this.height) {
                        var l = this.data[m + d][n + e];
                        if (l) {
                            this.tiles.drawTile(n * this.tilesize - j, m * this.tilesize - f, l - 1, this.tilesize)
                        }
                    }
                }
            }
            ig.system.context = q;
            return o
        },
        draw: function () {
            if (!this.tiles.loaded || !this.enabled) {
                return
            }
            if (this.preRender) {
                this.drawPreRendered()
            } else {
                this.drawTiled()
            }
        },
        drawPreRendered: function () {
            if (!this.preRenderedChunks) {
                this.preRenderMapToChunks()
            }
            var r = ig.system.getDrawPos(this.scroll.x), q = ig.system.getDrawPos(this.scroll.y);
            if (this.repeat) {
                var n = this.width * this.tilesize * ig.system.scale;
                r = (r % n + n) % n;
                var e = this.height * this.tilesize * ig.system.scale;
                q = (q % e + e) % e
            }
            var j = Math.max(Math.floor(r / this.chunkSize), 0), g = Math.max(Math.floor(q / this.chunkSize), 0), i = Math.ceil((r + ig.system.realWidth) / this.chunkSize), f = Math.ceil((q + ig.system.realHeight) / this.chunkSize), b = this.preRenderedChunks[0].length, a = this.preRenderedChunks.length;
            if (!this.repeat) {
                i = Math.min(i, b);
                f = Math.min(f, a)
            }
            var o = 0;
            for (var c = g; c < f; c++) {
                var p = 0;
                for (var d = j; d < i; d++) {
                    var m = this.preRenderedChunks[c % a][d % b];
                    var l = -r + d * this.chunkSize - p;
                    var k = -q + c * this.chunkSize - o;
                    ig.system.context.drawImage(m, l, k);
                    ig.Image.drawCount++;
                    if (this.debugChunks) {
                        ig.system.context.strokeStyle = "#f0f";
                        ig.system.context.strokeRect(l, k, this.chunkSize, this.chunkSize)
                    }
                    if (this.repeat && m.width < this.chunkSize && l + m.width < ig.system.realWidth) {
                        p += this.chunkSize - m.width;
                        i++
                    }
                }
                if (this.repeat && m.height < this.chunkSize && k + m.height < ig.system.realHeight) {
                    o += this.chunkSize - m.height;
                    f++
                }
            }
        },
        drawTiled: function () {
            var j = 0, e = null, g = (this.scroll.x / this.tilesize).toInt(), f = (this.scroll.y / this.tilesize).toInt(), b = this.scroll.x % this.tilesize, p = this.scroll.y % this.tilesize, i = -b - this.tilesize, h = -p - this.tilesize, d = ig.system.width + this.tilesize - b, c = ig.system.height + this.tilesize - p;
            for (var m = -1, k = h; k < c; m++, k += this.tilesize) {
                var o = m + f;
                if (o >= this.height || o < 0) {
                    if (!this.repeat) {
                        continue
                    }
                    o = (o % this.height + this.height) % this.height
                }
                for (var n = -1, l = i; l < d; n++, l += this.tilesize) {
                    var a = n + g;
                    if (a >= this.width || a < 0) {
                        if (!this.repeat) {
                            continue
                        }
                        a = (a % this.width + this.width) % this.width
                    }
                    if ((j = this.data[o][a])) {
                        if ((e = this.anims[j - 1])) {
                            e.draw(l, k)
                        } else {
                            this.tiles.drawTile(l, k, j - 1, this.tilesize)
                        }
                    }
                }
            }
        }
    })
});
ig.baked = true;
ig.module("impact.game").requires("impact.impact", "impact.entity", "impact.collision-map", "impact.background-map").defines(function () {
    ig.Game = ig.Class.extend({
        clearColor: "#000000",
        gravity: 0,
        screen: {x: 0, y: 0},
        _rscreen: {x: 0, y: 0},
        entities: [],
        namedEntities: {},
        collisionMap: ig.CollisionMap.staticNoCollision,
        backgroundMaps: [],
        backgroundAnims: {},
        autoSort: false,
        sortBy: null,
        cellSize: 64,
        _deferredKill: [],
        _levelToLoad: null,
        _doSortEntities: false,
        staticInstantiate: function () {
            this.sortBy = this.sortBy || ig.Game.SORT.Z_INDEX;
            ig.game = this;
            return null
        },
        loadLevel: function (e) {
            this.screen = {x: 0, y: 0};
            this.entities = [];
            this.namedEntities = {};
            for (var b = 0; b < e.entities.length; b++) {
                var d = e.entities[b];
                this.spawnEntity(d.type, d.x, d.y, d.settings)
            }
            this.sortEntities();
            this.collisionMap = ig.CollisionMap.staticNoCollision;
            this.backgroundMaps = [];
            for (var b = 0; b < e.layer.length; b++) {
                var c = e.layer[b];
                if (c.name == "collision") {
                    this.collisionMap = new ig.CollisionMap(c.tilesize, c.data)
                } else {
                    var a = new ig.BackgroundMap(c.tilesize, c.data, c.tilesetName);
                    a.anims = this.backgroundAnims[c.tilesetName] || {};
                    a.repeat = c.repeat;
                    a.distance = c.distance;
                    a.foreground = !!c.foreground;
                    a.preRender = !!c.preRender;
                    a.name = c.name;
                    this.backgroundMaps.push(a)
                }
            }
            for (var b = 0; b < this.entities.length; b++) {
                this.entities[b].ready()
            }
        },
        loadLevelDeferred: function (a) {
            this._levelToLoad = a
        },
        getMapByName: function (a) {
            if (a == "collision") {
                return this.collisionMap
            }
            for (var b = 0; b < this.backgroundMaps.length; b++) {
                if (this.backgroundMaps[b].name == a) {
                    return this.backgroundMaps[b]
                }
            }
            return null
        },
        getEntityByName: function (a) {
            return this.namedEntities[a]
        },
        getEntitiesByType: function (e) {
            var c = typeof(e) === "string" ? ig.global[e] : e;
            var b = [];
            for (var d = 0; d < this.entities.length; d++) {
                var f = this.entities[d];
                if (f instanceof c && !f._killed) {
                    b.push(f)
                }
            }
            return b
        },
        spawnEntity: function (d, a, f, c) {
            var b = typeof(d) === "string" ? ig.global[d] : d;
            if (!b) {
                throw ("Can't spawn entity of type " + d)
            }
            var e = new (b)(a, f, c || {});
            this.entities.push(e);
            if (e.name) {
                this.namedEntities[e.name] = e
            }
            return e
        },
        sortEntities: function () {
            this.entities.sort(this.sortBy)
        },
        sortEntitiesDeferred: function () {
            this._doSortEntities = true
        },
        removeEntity: function (a) {
            if (a.name) {
                delete this.namedEntities[a.name]
            }
            a._killed = true;
            a.type = ig.Entity.TYPE.NONE;
            a.checkAgainst = ig.Entity.TYPE.NONE;
            a.collides = ig.Entity.COLLIDES.NEVER;
            this._deferredKill.push(a)
        },
        run: function () {
            this.update();
            this.draw()
        },
        update: function () {
            if (this._levelToLoad) {
                this.loadLevel(this._levelToLoad);
                this._levelToLoad = null
            }
            this.updateEntities();
            this.checkEntities();
            for (var d = 0; d < this._deferredKill.length; d++) {
                this._deferredKill[d].erase();
                this.entities.erase(this._deferredKill[d])
            }
            this._deferredKill = [];
            if (this._doSortEntities || this.autoSort) {
                this.sortEntities();
                this._doSortEntities = false
            }
            for (var e in this.backgroundAnims) {
                var b = this.backgroundAnims[e];
                for (var c in b) {
                    b[c].update()
                }
            }
        },
        updateEntities: function () {
            for (var a = 0; a < this.entities.length; a++) {
                var b = this.entities[a];
                if (!b._killed) {
                    b.update()
                }
            }
        },
        draw: function () {
            if (this.clearColor) {
                ig.system.clear(this.clearColor)
            }
            this._rscreen.x = ig.system.getDrawPos(this.screen.x) / ig.system.scale;
            this._rscreen.y = ig.system.getDrawPos(this.screen.y) / ig.system.scale;
            var a;
            for (a = 0; a < this.backgroundMaps.length; a++) {
                var b = this.backgroundMaps[a];
                if (b.foreground) {
                    break
                }
                b.setScreenPos(this.screen.x, this.screen.y);
                b.draw()
            }
            this.drawEntities();
            for (a; a < this.backgroundMaps.length; a++) {
                var b = this.backgroundMaps[a];
                b.setScreenPos(this.screen.x, this.screen.y);
                b.draw()
            }
        },
        drawEntities: function () {
            for (var a = 0; a < this.entities.length; a++) {
                this.entities[a].draw()
            }
        },
        checkEntities: function () {
            var f = {};
            for (var h = 0; h < this.entities.length; h++) {
                var d = this.entities[h];
                if (d.type == ig.Entity.TYPE.NONE && d.checkAgainst == ig.Entity.TYPE.NONE && d.collides == ig.Entity.COLLIDES.NEVER) {
                    continue
                }
                var l = {}, a = Math.floor(d.pos.x / this.cellSize), m = Math.floor(d.pos.y / this.cellSize), g = Math.floor((d.pos.x + d.size.x) / this.cellSize) + 1, b = Math.floor((d.pos.y + d.size.y) / this.cellSize) + 1;
                for (var k = a; k < g; k++) {
                    for (var j = m; j < b; j++) {
                        if (!f[k]) {
                            f[k] = {};
                            f[k][j] = [d]
                        } else {
                            if (!f[k][j]) {
                                f[k][j] = [d]
                            } else {
                                var n = f[k][j];
                                for (var i = 0; i < n.length; i++) {
                                    if (d.touches(n[i]) && !l[n[i].id]) {
                                        l[n[i].id] = true;
                                        ig.Entity.checkPair(d, n[i])
                                    }
                                }
                                n.push(d)
                            }
                        }
                    }
                }
            }
        }
    });
    ig.Game.SORT = {
        Z_INDEX: function (d, c) {
            return d.zIndex - c.zIndex
        }, POS_X: function (d, c) {
            return (d.pos.x + d.size.x) - (c.pos.x + c.size.x)
        }, POS_Y: function (d, c) {
            return (d.pos.y + d.size.y) - (c.pos.y + c.size.y)
        }
    }
});
ig.baked = true;
ig.module("game.gui.button").requires("impact.image", "impact.entity").defines(function () {
    window.GameButtonManager = ig.Class.extend({
        buttons: [],
        nameButtons: {},
        buttonIndex: 0,
        action: "defaultAction",
        staticInstantiate: function (a) {
            if (GameButtonManager.instance) {
                if (a) {
                    GameButtonManager.instance.clear()
                }
                return GameButtonManager.instance
            } else {
                return null
            }
        },
        init: function (c) {
            if (c instanceof Array) {
                for (var b = 0; c.length; b++) {
                    var a = c[b];
                    if (!c[b].name) {
                        a.name = "button_" + this.buttonIndex
                    }
                    if (!this.nameButtons[a]) {
                        this.nameButtons[a.name] = a;
                        this.buttons.push(a)
                    }
                    this.buttonIndex++
                }
                this.sortButtons()
            }
            this.pressed = false;
            this.released = true;
            if (this.action) {
                ig.input.bind(ig.KEY.MOUSE1, this.action)
            }
            GameButtonManager.instance = this
        },
        addButton: function (a) {
            if (a instanceof GameButton) {
                if (!a.name) {
                    a.name = "button_" + this.buttonIndex
                }
                if (!this.nameButtons[a.name]) {
                    this.buttonIndex++;
                    this.buttons.push(a);
                    this.nameButtons[a.name] = a;
                    this.sortButtons()
                }
            }
        },
        sortButtons: function () {
            this.buttons.sort(function (d, c) {
                return c.zIndex - d.zIndex
            })
        },
        removeButton: function (a) {
            if (this.nameButtons[a]) {
                var c = this.nameButtons[a];
                var d = this.buttons.length;
                for (var b = 0; b < d; b++) {
                    if (c == this.buttons[b]) {
                        this.buttons.splice(b, 1)
                    }
                }
                delete this.nameButtons[a]
            }
        },
        update: function () {
            if (ig.input.pressed(this.action)) {
                this.pressed = true;
                this.released = false;
                this.oldPointer = ig.input.mouse;
                var a = this.checkInButton(this.oldPointer.x, this.oldPointer.y);
                if (a) {
                    this.pressedBtn = a;
                    this.pressedBtn.trigger("pressed")
                }
            }
            if (ig.input.released(this.action)) {
                this.pressed = false;
                this.released = true;
                var a = this.checkInButton(ig.input.mouse.x, ig.input.mouse.y);
                if (a) {
                    this.releasedBtn = a;
                    this.releasedBtn.trigger("released")
                }
                if (this.pressedBtn && this.releasedBtn != this.pressedBtn) {
                    this.pressedBtn.trigger("released")
                }
            }
            if (this.pressed) {
                var b = ig.input.mouse;
                if (b.x != this.oldPointer.x || b.y != this.oldPointer.y) {
                    var a = this.checkInButton(b.x, b.y);
                    if (a == this.pressedBtn) {
                        this.pressedBtn.trigger("move")
                    } else {
                        this.pressedBtn = null
                    }
                }
            }
            if (this.pressedBtn && this.pressedBtn && this.pressedBtn == this.releasedBtn) {
                this.releasedBtn.trigger("click");
                this.releasedBtn = null;
                this.pressedBtn = null
            }
            if (!this.pressed && this.released) {
                this.releasedBtn = null;
                this.pressedBtn = null
            }
        },
        checkInButton: function (a, f) {
            var e = this.buttons.length;
            for (var c = 0; c < e; c++) {
                var b = this.buttons[c];
                var d = b.area;
                if (a > d.x1 && a < d.x2 && f > d.y1 && f < d.y2) {
                    return b
                }
            }
            return null
        },
        checkMove: function () {
            if (Math.abs(this.oldPointer.x - ig.input.mouse.x) > 10 || Math.abs(this.oldPointer.y - ig.input.mouse.y) > 10) {
                return true
            }
            return false
        },
        clear: function () {
            this.buttons = [];
            this.nameButtons = {}
        },
        clearActionBtn: function () {
            this.pressedBtn = null;
            this.releasedBtn = null
        }
    });
    GameButtonManager.instance = null;
    window.GameButton = ig.Entity.extend({
        text: "", zIndex: 1, init: function (a, c, b) {
            this.parent(a, c, b);
            this.bg = b.bg || null;
            this.text = b.text || "";
            this.name = b.name || "";
            this.fontStyle = b.fontStyle || "#000";
            this.pressed = false;
            this.released = false;
            this.callback = b.callback || null;
            this.pressedCallback = b.pressedCallback || null;
            this.releasedCallback = b.releasedCallback || null;
            this.moveCallback = b.moveCallback || null;
            this.area = {x1: this.pos.x, y1: this.pos.y, x2: this.pos.x + this.size.x, y2: this.pos.y + this.size.y};
            new GameButtonManager().addButton(this)
        }, trigger: function (a) {
            if (a == "click") {
                this.callback && this.callback.call(this);
                return
            }
            a += "Callback";
            this[a] && this[a].call(this)
        }, draw: function () {
            this.parent();
            if (this.bg) {
                this.bg.draw(this.pos.x, this.pos.y)
            }
            if (this.text) {
                var c = ig.system.context;
                c.save();
                c.fillStyle = this.fontStyle;
                c.textAlign = "center";
                c.font = 'normal 36px "Microsoft YaHei"';
                var b = this.pos.x + this.size.x / 2;
                var a = this.pos.y + this.size.y * 2 / 3;
                c.fillText(this.text, b, a);
                c.restore()
            }
        }, checkClick: function (a, b) {
            if (a > this.area.x1 && a < this.area.x2 && b > this.area.y1 && b < this.area.y2) {
                return true
            } else {
                return false
            }
        }, kill: function () {
            this.bg = null;
            this.callback = null;
            this.pressedCallback = null;
            this.releasedCallback = null;
            this.moveCallback = null;
            new GameButtonManager().removeButton(this.name);
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.gui.topbar").requires("impact.image", "impact.entity").defines(function () {
    window.GameTopbar = ig.Entity.extend({
        icons: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/bar_icon.png"),
        text: "",
        zIndex: 4,
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.pos = {x: 0, y: 0};
            this.width = ig.system.width;
            this.height = ig.system.height;
            this.score = TankGame.gameInfo.totalScore;
            this.addNum = 0;
            this.recordBtn = ig.game.spawnEntity(GameButton, this.width - 448, 0, {
                size: {x: 400, y: 46},
                zIndex: 4,
                name: "record",
                callback: function () {
                    var d = ig.game.getEntitiesByType(Recordpanel);
                    if (!d.length) {
                        ig.game.spawnEntity(Recordpanel, 0, 0);
                        ig.game.sortEntitiesDeferred()
                    }
                }
            });
            this.loadingColors = ["rgba(19, 96, 133, 1)", "rgba(19, 96, 133, 0.85)", "rgba(19, 96, 133, 0.7)", "rgba(19, 96, 133, 0.55)", "rgba(19, 96, 133, 0.4)", "rgba(19, 96, 133, 0.25)", "rgba(19, 96, 133, 0.1)", "rgba(19, 96, 133, 0)"];
            this.loadingColorIndex = 0;
            this.loadingTime = ig.system.clock.last
        },
        updateScore: function (a) {
            var b = TankGame.gameInfo.totalScore - this.score;
            if (!a) {
                this.addNum = b
            } else {
                this.score = TankGame.gameInfo.totalScore
            }
        },
        update: function () {
            this.parent();
            if (this.addNum != 0) {
                var a = 1;
                if (this.addNum < 0) {
                    a = -1
                }
                this.addNum -= a;
                this.score += a
            }
        },
        draw: function () {
            this.parent();
            this.icons.drawTile(46 * 2 / 3, 3, 0, 46, 46);
            var c = ig.system.context;
            c.save();
            c.fillStyle = "#fff";
            c.textAlign = "left";
            c.font = 'normal 24px "Microsoft YaHei"';
            var b = 46 * 2 + 10;
            var a = 46 * 3 / 4;
            if (TankGame.loadedUserScore) {
                c.fillText("积分余额：" + this.score, b, a)
            } else {
                c.fillText("积分余额：", b, a)
            }
            c.restore();
            if (!TankGame.loadedUserScore) {
                this.drawLoading(b + 120, a - 20)
            }
            c.save();
            c.fillStyle = "#fe9e00";
            c.textAlign = "left";
            c.font = 'normal 24px "Microsoft YaHei"';
            var d = c.measureText(this.distanceUnit);
            var e = d.width;
            var b = this.width - e - 46 / 2;
            var a = 46 * 3 / 4;
            c.fillText("闯关记录", b, a);
            c.restore();
            this.icons.drawTile(b - 46 - 10, 3, 1, 46, 46)
        },
        drawLoading: function (a, f) {
            var b = ig.system.clock.last;
            if (b - this.loadingTime > 0.1) {
                this.loadingTime = b;
                this.loadingColorIndex++;
                if (this.loadingColorIndex >= this.loadingColors.length) {
                    this.loadingColorIndex = 0
                }
            }
            var c = this.loadingColors.length;
            var d = 6, e = d;
            this._drawBlock(a, f + e * 2, d, e, this.loadingColors[(this.loadingColorIndex + 7) % c]);
            this._drawBlock(a + d, f + e, d, e, this.loadingColors[(this.loadingColorIndex + 6) % c]);
            this._drawBlock(a + d * 2, f, d, e, this.loadingColors[(this.loadingColorIndex + 5) % c]);
            this._drawBlock(a + d * 3, f + e, d, e, this.loadingColors[(this.loadingColorIndex + 4) % c]);
            this._drawBlock(a + d * 4, f + e * 2, d, e, this.loadingColors[(this.loadingColorIndex + 3) % c]);
            this._drawBlock(a + d * 3, f + e * 3, d, e, this.loadingColors[(this.loadingColorIndex + 2) % c]);
            this._drawBlock(a + d * 2, f + e * 4, d, e, this.loadingColors[(this.loadingColorIndex + 1) % c]);
            this._drawBlock(a + d, f + e * 3, d, e, this.loadingColors[this.loadingColorIndex])
        },
        _drawBlock: function (b, f, e, a, d) {
            var c = ig.system.context;
            c.save();
            c.fillStyle = d;
            c.lineWidth = 0;
            c.beginPath();
            c.moveTo(b, f);
            c.lineTo(b + e, f);
            c.lineTo(b + e, f + a);
            c.lineTo(b, f + a);
            c.fill();
            c.restore()
        },
    })
});
ig.baked = true;
ig.module("game.gui.resultpanel").requires("impact.image", "impact.entity", "game.gui.button", "impact.font").defines(function () {
    window.Resultpanel = ig.Entity.extend({
        bg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/result_bg.png"),
        font3: new ig.Font("/IMSPActivities/resources/activities/pointgames/media/font1.png"),
        font4: new ig.Font("/IMSPActivities/resources/activities/pointgames/media/font2.png"),
        againBtnImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/again.png"),
        shareBtnImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/share.png"),
        closeBtnImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/closeBtn.png"),
        size: {x: 537, y: 338},
        zIndex: 3,
        init: function (a, d, b) {
            this.parent(a, d, b);
            this.pos = {x: (ig.system.width - this.size.x) / 2, y: (ig.system.height - this.size.y) / 2};
            this.font3.firstChar = 46;
            this.font4.firstChar = 48;
            this.number = TankGame.gameInfo.number;
            this.score = TankGame.gameInfo.fetchScore();
            this.total = TankGame.gameInfo.total;
            this.percent = TankGame.gameInfo.percent;
            if (TankGame.params.from == 161) {
                this.againBtn = ig.game.spawnEntity(GameButton, this.pos.x + this.size.x / 3 - 79, this.pos.y + this.size.y * 2 / 3 - 29, {
                    bg: this.againBtnImg,
                    size: {x: 157, y: 58},
                    zIndex: 4,
                    callback: function () {
                        TankGame.payScoreToPlay()
                    }
                });
                this.shareBtn = ig.game.spawnEntity(GameButton, this.pos.x + this.size.x * 2 / 3 - 79, this.pos.y + this.size.y * 2 / 3 - 29, {
                    bg: this.shareBtnImg,
                    size: {x: 157, y: 58},
                    zIndex: 4,
                    callback: function () {
                        wx.ready(function () {
                            shareTitle = "9分坦克战-我击毁" + totalnum + "架坦克，你呢？";
                            var e = {
                                title: shareTitle,
                                desc: "玩游戏，赢积分，游戏玩得越好，积分赢得越多。",
                                imgUrl: image_url,
                                link: link_url,
                                success: function () {
                                },
                                cancel: function () {
                                }
                            };
                            wx.onMenuShareAppMessage(tar.shapeShareAppMessage(e));
                            wx.onMenuShareTimeline(tar.shapeShareTimeline(e))
                        });
                        ig.game.spawnEntity(Sharepanel, 0, 0);
                        ig.game.sortEntities()
                    }
                })
            } else {
                this.againBtn = ig.game.spawnEntity(GameButton, this.pos.x + (this.size.x - this.againBtnImg.width) / 2, this.pos.y + this.size.y * 2 / 3 - 29, {
                    bg: this.againBtnImg,
                    size: {x: 157, y: 58},
                    zIndex: 4,
                    callback: function () {
                        TankGame.payScoreToPlay()
                    }
                })
            }
            var c = this;
            this.closeBtn = ig.game.spawnEntity(GameButton, this.pos.x + this.size.x - this.closeBtnImg.width / 2, this.pos.y - this.closeBtnImg.height / 2, {
                bg: this.closeBtnImg,
                size: {x: this.closeBtnImg.width, y: this.closeBtnImg.height},
                zIndex: 4,
                callback: function () {
                    ig.system.setGame(TankGame.Start)
                }
            })
        },
        update: function () {
            this.parent()
        },
        draw: function () {
            this.parent();
            var a = ig.system.context;
            a.save();
            a.fillStyle = "rgba(0,0,0, 0.5)";
            a.fillRect(0, 0, ig.system.width, ig.system.height);
            a.restore();
            this.bg.draw(this.pos.x, this.pos.y);
            var b = ig.Font.ALIGN.CENTER;
            this.font3.draw(":;<=>?@A" + this.number + "BCDEF", this.pos.x + this.size.x / 2, this.pos.y + 60, b);
            this.font4.draw(":;" + this.score + "<=>", this.pos.x + this.size.x / 2, this.pos.y + 110, b);
            this.font3.draw("GH" + this.percent + "JEFK", this.pos.x + this.size.x / 2, this.pos.y + 280, b)
        },
        kill: function () {
            this.closeBtn.kill();
            this.againBtn.kill();
            if (TankGame.params.from == 161) {
                this.shareBtn.kill()
            }
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.gui.recordpanel").requires("impact.image", "impact.entity", "game.gui.button", "impact.font").defines(function () {
    window.Recordpanel = ig.Entity.extend({
        bg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/bg1.png"),
        loading: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/loading.png"),
        loadAll: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/loadall.png"),
        title: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/record_t.png"),
        font3: new ig.Font("/IMSPActivities/resources/activities/pointgames/media/font3.png"),
        font4: new ig.Font("/IMSPActivities/resources/activities/pointgames/media/font4.png"),
        closeBtnImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/closeBtn.png"),
        size: {x: 537, y: 338},
        action: "scrollScore",
        zIndex: 5,
        init: function (a, e, c) {
            this.parent(a, e, c);
            this.pos = {x: 0, y: 0};
            this.size = {x: ig.system.width, y: ig.system.height};
            this.font3.firstChar = 48;
            this.font4.firstChar = 45;
            this.number = TankGame.gameInfo.number;
            this.total = TankGame.gameInfo.total;
            this.percent = TankGame.gameInfo.percent;
            this.loadingColors = ["rgba(19, 96, 133, 1)", "rgba(19, 96, 133, 0.85)", "rgba(19, 96, 133, 0.7)", "rgba(19, 96, 133, 0.55)", "rgba(19, 96, 133, 0.4)", "rgba(19, 96, 133, 0.25)", "rgba(19, 96, 133, 0.1)", "rgba(19, 96, 133, 0)"];
            this.loadingColorIndex = 0;
            this.loadingTime = ig.system.clock.last;
            this.scrollEnabled = false;
            var d = this;
            this.closeBtn = ig.game.spawnEntity(GameButton, this.pos.x + this.size.x - this.closeBtnImg.width, this.pos.y, {
                bg: this.closeBtnImg,
                size: {x: this.closeBtnImg.width, y: this.closeBtnImg.height},
                zIndex: 6,
                callback: function () {
                    d.kill()
                }
            });
            var d = this;
            this.scrollBtn = ig.game.spawnEntity(GameButton, 0, this.pos.y + 46 * 6.7, {
                size: {
                    x: this.size.x,
                    y: this.size.y - 46 * 9
                }, zIndex: 6, pressedCallback: function () {
                    if (!d.scrollEnabled) {
                        d.old = {x: ig.input.mouse.x, y: ig.input.mouse.y};
                        d.scrollEnabled = true
                    }
                }, releasedCallback: function () {
                    d.scrollEnabled = false;
                    d.old = {x: 0, y: 0};
                    if (d.scrollTop < 0) {
                        d.scrollTo(0, 50)
                    } else {
                        if (d.scrollTop + d.boxHeight > d.contentHeight) {
                            if (d.scrollTop + d.boxHeight - d.contentHeight > 50) {
                                if (!d.showLoadAll) {
                                    d.loadData(d.page);
                                    d.scrollTo(d.contentHeight - d.boxHeight + d.lineHeight, 50)
                                } else {
                                    if (d.showLoadAll) {
                                        d.scrollTo(d.contentHeight - d.boxHeight + d.lineHeight, 50)
                                    } else {
                                        d.scrollTo(d.contentHeight - d.boxHeight, 50)
                                    }
                                }
                            }
                        }
                    }
                }
            });
            this.grasses = [];
            var b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 92, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 138, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 46 * 8, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 46 * 10, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 46 * 11, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            b = ig.game.spawnEntity(EntityTerrain, this.pos.x + 46 * 12, this.pos.y + this.size.y - 46, {zIndex: 6});
            this.grasses.push(b);
            this.scrollTop = 0;
            this.lineHeight = 46;
            this.startBoxY = this.pos.y + 46 * 7;
            this.endBoxY = this.pos.y + this.size.y - 46 * 2;
            this.boxHeight = this.endBoxY - this.startBoxY;
            this.contentHeight = 0;
            this.scrollSpeed = 0;
            this.scrollAccl = 0;
            this.scrollDirect = 0;
            this.data = [];
            this.showLoading = true;
            this.showLoadAll = false;
            this.page = 1;
            this.loadData(this.page)
        },
        addData: function (a) {
            if (a instanceof Array) {
                this.data = this.data.concat(a);
                this.contentHeight = this.data.length * this.lineHeight
            }
        },
        loadData: function (b) {
            var a = this;
            if (TankGame.bindStatus != 3) {
                if (TankGame.params.from == "161") {
                    window.location.href = "http://market.cmbchina.com/ccard/weixin/news/zjy/wkf_5.html";
                    return false
                } else {
                    window.location.href = "http://xyk.cmbchina.com/Latte/tips/zfb/20150706";
                    return false
                }
            }
            TankGame.ajax({
                type: "post",
                url: "/IMSPActivities/pointgames/queryRecord",
                data: TankGame.params.getUrlParams({pageNum: b, pageSize: 15}),
                success: function (g) {
                    if (g.returnCode == "0000") {
                        g = g.records || [];
                        var d = [];
                        for (var e = 0, c = g.length; e < c; e++) {
                            var f = g[e];
                            d.push({name: f.gameName, time: f.gameTime, score: f.gamePoints})
                        }
                        a.addData(d);
                        a.hideLoadTip();
                        a.page++;
                        if (!d.length) {
                            a.showAll()
                        }
                    } else {
                        TankGame.dealReturnCode(g)
                    }
                }
            })
        },
        showLoadTip: function () {
            this.showLoading = true
        },
        hideLoadTip: function () {
            this.showLoading = false
        },
        showAll: function () {
            this.showLoadAll = true
        },
        scrollTo: function (b, a) {
            this.scrollAccl = (b - this.scrollTop) * 2 / Math.pow(a, 2);
            this.scrollSpeed = this.scrollAccl * a;
            this.scrollToY = b;
            if (this.scrollSpeed > 0) {
                this.scrollDirect = -1
            } else {
                this.scrollDirect = 1
            }
        },
        update: function () {
            this.parent();
            if (this.scrollEnabled && this.contentHeight > this.boxHeight) {
                this.scrollTop += this.old.y - ig.input.mouse.y;
                this.old = {x: ig.input.mouse.x, y: ig.input.mouse.y}
            }
            if (this.scrollSpeed != 0) {
                this.scrollTop += this.scrollSpeed;
                this.scrollSpeed -= this.scrollAccl
            } else {
                this.scrollSpeed = 0;
                this.scrollAccl = 0
            }
            if (this.scrollDirect == -1 && this.scrollTop >= 0) {
                this.scrollTop = 0;
                this.scrollSpeed = 0;
                this.scrollDirect = 0;
                this.scrollAccl = 0
            } else {
                if (this.scrollDirect == 1 && this.scrollTop <= this.scrollToY) {
                    this.scrollSpeed = 0;
                    this.scrollAccl = 0;
                    this.scrollDirect = 0;
                    this.scrollTop = this.scrollToY
                }
            }
        },
        draw: function () {
            var c = ig.system.context;
            c.save();
            c.fillStyle = "#ffe4c3";
            c.fillRect(this.pos.x, this.pos.y, this.size.x, this.size.y);
            c.restore();
            c.save();
            var h = c.createPattern(this.bg.data, "repeat-y");
            c.fillStyle = h;
            c.fillRect(0, this.pos.y, 34, this.size.y);
            c.restore();
            c.save();
            c.fillStyle = h;
            c.translate(this.pos.x + this.size.x - 68, this.pos.y);
            c.fillRect(34, 0, 34, this.size.y);
            c.restore();
            var f = this.data.length;
            if (f) {
                var g = this.scrollTop > 0 ? Math.floor(this.scrollTop / this.lineHeight) : 0;
                var e = Math.ceil((this.scrollTop + this.boxHeight) / this.lineHeight);
                e = e < f ? e : f;
                var a = this.startBoxY - (this.scrollTop < 0 ? this.scrollTop : this.scrollTop % this.lineHeight);
                for (var d = g; d < e; d++) {
                    var b = this.data[d];
                    a += this.lineHeight * (d > g ? 1 : 0);
                    this.font4.draw("9?@", this.pos.x + 46 * 3, a, ig.Font.ALIGN.LEFT);
                    this.font4.draw(b.score + "?@", this.pos.x + this.size.x / 2 + 23, a, ig.Font.ALIGN.CENTER);
                    this.font4.draw(b.time, this.pos.x + this.size.x - 46 * 2, a, ig.Font.ALIGN.RIGHT)
                }
            }
            c.save();
            c.fillStyle = "#ffe4c3";
            c.fillRect(this.pos.x + 46, this.pos.y, this.size.x - 46 * 2, this.startBoxY);
            c.restore();
            this.title.draw(this.pos.x + (this.size.x - this.title.width) / 2, this.pos.y + 46 * 2);
            this.font3.draw("0145", this.pos.x + 46 * 2, this.pos.y + 46 * 5.5, ig.Font.ALIGN.LEFT);
            this.font3.draw("2345", this.pos.x + this.size.x / 2 + 23, this.pos.y + 46 * 5.5, ig.Font.ALIGN.CENTER);
            this.font3.draw("67", this.pos.x + this.size.x - 46 * 2, this.pos.y + 46 * 5.5, ig.Font.ALIGN.RIGHT);
            c.save();
            c.fillStyle = "#ffe4c3";
            c.fillRect(this.pos.x + 46, this.endBoxY, this.size.x - 46 * 2, 46 * 2);
            c.restore();
            c.save();
            c.strokeStyle = "#000";
            c.lineWidth = 3;
            c.beginPath();
            c.moveTo(this.pos.x + 46 * 2, this.pos.y + 46 * 4.5);
            c.lineTo(this.pos.x + this.size.x - 46 * 2, this.pos.y + 46 * 4.5);
            c.stroke();
            c.restore();
            this.drawLoading();
            if (this.showLoadAll && this.scrollTop + this.boxHeight - this.contentHeight >= 40) {
                this.loadAll.draw(this.pos.x + (this.size.x - this.loadAll.width) / 2, this.pos.y + this.size.y - this.loadAll.height - this.lineHeight * 2)
            }
            this.parent()
        },
        drawLoading: function () {
            if (this.showLoading && this.scrollTop + this.boxHeight - this.contentHeight >= 40) {
                var b = ig.system.clock.last;
                if (b - this.loadingTime > 0.1) {
                    this.loadingTime = b;
                    this.loadingColorIndex++;
                    if (this.loadingColorIndex >= this.loadingColors.length) {
                        this.loadingColorIndex = 0
                    }
                }
                var c = this.loadingColors.length;
                var a = this.pos.x + (this.size.x - this.loading.width) / 2 - 46;
                var f = this.pos.y + this.size.y - this.loading.height - 46 * 2;
                var d = 6, e = d;
                this._drawBlock(a, f + e * 2, d, e, this.loadingColors[(this.loadingColorIndex + 7) % c]);
                this._drawBlock(a + d, f + e, d, e, this.loadingColors[(this.loadingColorIndex + 6) % c]);
                this._drawBlock(a + d * 2, f, d, e, this.loadingColors[(this.loadingColorIndex + 5) % c]);
                this._drawBlock(a + d * 3, f + e, d, e, this.loadingColors[(this.loadingColorIndex + 4) % c]);
                this._drawBlock(a + d * 4, f + e * 2, d, e, this.loadingColors[(this.loadingColorIndex + 3) % c]);
                this._drawBlock(a + d * 3, f + e * 3, d, e, this.loadingColors[(this.loadingColorIndex + 2) % c]);
                this._drawBlock(a + d * 2, f + e * 4, d, e, this.loadingColors[(this.loadingColorIndex + 1) % c]);
                this._drawBlock(a + d, f + e * 3, d, e, this.loadingColors[this.loadingColorIndex]);
                this.loading.draw(this.pos.x + (this.size.x - this.loading.width) / 2, this.pos.y + this.size.y - this.loading.height - this.lineHeight * 2)
            }
        },
        _drawBlock: function (b, f, e, a, d) {
            var c = ig.system.context;
            c.save();
            c.fillStyle = d;
            c.lineWidth = 0;
            c.beginPath();
            c.moveTo(b, f);
            c.lineTo(b + e, f);
            c.lineTo(b + e, f + a);
            c.lineTo(b, f + a);
            c.fill();
            c.restore()
        },
        kill: function () {
            this.closeBtn.kill();
            this.scrollBtn.kill();
            for (var a = 0; a < this.grasses.length; a++) {
                this.grasses[a].kill()
            }
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.entities.terrain").requires("impact.image", "impact.entity").defines(function () {
    window.EntityTerrain = ig.Entity.extend({
        image: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/terrain.png"),
        width: 46,
        height: 46,
        size: {x: 46, y: 46},
        tile: 0,
        init: function (a, c, b) {
            this.parent(a, c, b)
        },
        draw: function () {
            this.parent();
            this.image.drawTile(this.pos.x - this.offset.x, this.pos.y - this.offset.y, this.tile, this.width, this.height)
        }
    })
});
ig.baked = true;
ig.module("game.entities.enemy").requires("impact.image", "impact.entity").defines(function () {
    window.EnemyShell = ig.Entity.extend({
        image: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/shell.png"),
        width: 14,
        height: 24,
        size: {x: 14, y: 24},
        tile: 1,
        vel: {x: 0, y: 500},
        maxVel: {x: 0, y: 500},
        checkAgainst: ig.Entity.TYPE.B,
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.tile = 1;
            this.myPos = {x: this.pos.x - ig.game._rscreen.x, y: this.pos.y - ig.game._rscreen.y}
        },
        update: function () {
            this.parent();
            var b = ig.game._rscreen.x;
            var a = ig.game._rscreen.y;
            this.myPos = {x: this.pos.x - b, y: this.pos.y - a};
            if (this.pos.y - a > ig.system.height) {
                this.kill()
            }
        },
        draw: function () {
            this.parent();
            this.image.drawTile(this.myPos.x, this.myPos.y, this.tile, this.width, this.height)
        },
        check: function (a) {
            if (a instanceof EntityPlayer && a.isHealth) {
                a.destroy();
                this.kill()
            }
        }
    });
    window.EnemyScore = ig.Entity.extend({
        animSheet: new ig.AnimationSheet("/IMSPActivities/resources/activities/pointgames/media/add.png", 54, 54),
        width: 54,
        height: 54,
        size: {x: 54, y: 54},
        vel: {x: 0, y: -200},
        maxVel: {x: 0, y: 200},
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.addAnim("run", 0.05, [0, 1, 2, 3, 4], true);
            this.currentAnim = this.anims.run
        },
        update: function () {
            this.parent();
            if (this.currentAnim.frame >= 5) {
                this.kill()
            }
        }
    });
    window.EntityEnemy = ig.Entity.extend({
        animSheet: new ig.AnimationSheet("/IMSPActivities/resources/activities/pointgames/media/tank.png", 92, 92),
        width: 92,
        height: 92,
        size: {x: 85, y: 74},
        offset: {x: 3, y: 2},
        vel: {x: 0, y: -TankGame.gameInfo.runSpeed},
        maxVel: {x: 0, y: TankGame.gameInfo.runSpeed},
        arriveY: 0,
        type: ig.Entity.TYPE.B,
        checkAgainst: ig.Entity.TYPE.A,
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.fireTime = ig.system.clock.last;
            this.isHealth = true;
            this.tankColor = b.tankColor || 0;
            this.health = this.tankColor + 1;
            if (this.health > 3) {
                this.health = 3
            }
            if (this.tankColor > 2) {
                this.tankColor = 2
            }
            this.setAnimate();
            this.setConfig()
        },
        setAnimate: function () {
            this.frameStartTile = this.tankColor * 3;
            this.addAnim("run", 0.1, [this.frameStartTile, this.frameStartTile + 1], false);
            this.addAnim("die", 0.1, [this.frameStartTile + 2], false);
            this.currentAnim = this.anims.run
        },
        setConfig: function () {
            var a = TankGame.difficulty;
            this.minShootTime = Math.max(0, 1 - a / 15);
            this.maxShootTime = Math.max(0, 2 - a / 10);
            this.diffShootTime = this.maxShootTime - this.minShootTime;
            this.nextTime = this.minShootTime + Math.random() * this.diffShootTime;
            this.nextTime = Math.min(this.nextTime, this.maxShootTime);
            this.difficulty = a
        },
        update: function () {
            this.parent();
            if (this.difficulty != TankGame.difficulty) {
                this.setConfig()
            }
            var b = ig.game._rscreen.y;
            if (ig.game.gameStatus != TankGame.STATUS.OVER) {
                var c = ig.system.clock.last;
                if (c - this.fireTime > this.nextTime && this.pos.y - b + this.height >= 46 && this.isHealth) {
                    this.nextTime = Math.min(this.minShootTime + Math.random() * this.diffShootTime, this.maxShootTime);
                    this.fireTime = c;
                    var a = this.pos.x + this.width / 2 - 7;
                    var d = this.pos.y + this.height - 12;
                    ig.game.spawnEntity(EnemyShell, a, d);
                    ig.game.sortEntities()
                }
            } else {
                this.vel.y = 0
            }
            if (this.vel.y != -this.maxVel.y && this.arriveY <= this.pos.y - b) {
                this.pos.y = this.arriveY + b;
                this.vel.y = -this.maxVel.y
            }
            if (!this.isHealth) {
                this.currentAnim = this.anims.die;
                if (c - this.destroyTime > 0.2) {
                    ig.game.addEnemy(this.pos.x, this.pos.y - 46 * 6, this.arriveY);
                    this.kill()
                }
            }
        },
        shoted: function () {
            this.health--;
            if (this.tankColor > 0) {
                this.tankColor--;
                this.setAnimate()
            }
            if (this.health <= 0) {
                this.tile--;
                this.destroy();
                TankGame.gameInfo.addNumber()
            }
        },
        destroy: function () {
            this.isHealth = false;
            this.destroyTime = ig.system.clock.last;
            var a = this.pos.x + this.width;
            var b = this.pos.y;
            ig.game.spawnEntity(EnemyScore, a, b);
            ig.game.sortEntitiesDeferred()
        }
    })
});
ig.baked = true;
ig.module("game.entities.bomb").requires("impact.image", "impact.entity").defines(function () {
    window.EntityBomb = ig.Entity.extend({
        image: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/bomb.png"),
        width: 56,
        height: 92,
        size: {x: 56, y: 92},
        tile: 0,
        vel: {x: 0, y: 500},
        maxVel: {x: 0, y: 500},
        checkAgainst: ig.Entity.TYPE.B,
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.tile = 0;
            this.myPos = {x: this.pos.x - ig.game._rscreen.x, y: this.pos.y - ig.game._rscreen.y}
        },
        update: function () {
            this.parent();
            var b = ig.game._rscreen.x;
            var a = ig.game._rscreen.y;
            this.myPos = {x: this.pos.x - b, y: this.pos.y - a};
            if (this.myPos.y > ig.system.height) {
                this.kill()
            }
        },
        draw: function () {
            this.parent();
            this.image.drawTile(this.myPos.x, this.myPos.y, this.tile, this.width, this.height)
        },
        check: function (a) {
            if (a instanceof EntityPlayer && a.isHealth) {
                a.destroy();
                this.kill()
            }
        }
    })
});
ig.baked = true;
ig.module("game.gui.loadergame").requires("impact.loader", "impact.image").defines(function () {
    window.LoaderGame = ig.Loader.extend({
        grass: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/grass.png"),
        tank: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/loadtank.png"),
        draw: function () {
            ig.system.clear("#000");
            this._drawStatus += (this.status - this._drawStatus) / 5;
            var e = ig.system.scale;
            var b = 46 * 6;
            var d = 46;
            var a = (ig.system.width * 0.5 - b / 2) * e;
            var g = (ig.system.height * 0.5 - d / 2) * e;
            b = b * e;
            d = d * e;
            var c = ig.system.context;
            var f = c.createPattern(this.grass.data, "repeat-x");
            c.save();
            c.fillStyle = f;
            c.translate(a, g);
            c.fillRect(0, 0, b * this._drawStatus, 46);
            c.restore();
            this.tank.draw(a + (b * this._drawStatus - 46), g, 0, 0)
        }
    })
});
ig.baked = true;
ig.module("game.gui.selectpanel").requires("impact.image", "impact.entity", "game.gui.button", "impact.font").defines(function () {
    window.SelectPanel = ig.Entity.extend({
        bg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/result_bg.png"),
        closeBtnImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/closeBtn.png"),
        size: {x: 537, y: 338},
        zIndex: 3,
        init: function (b, h, d) {
            this.parent(b, h, d);
            this.pos = {x: (ig.system.width - this.size.x) / 2, y: (ig.system.height - this.size.y) / 2};
            var c = TankGame.LevelData;
            var a = this.pos.y;
            this.btns = [], e = this;
            for (var g in c) {
                a += 46;
                var f = c[g];
                (function (j, i) {
                    var k = ig.game.spawnEntity(GameButton, e.pos.x + e.size.x / 2 - 25, a, {
                        text: j,
                        harder: i,
                        fontStyle: "#DE2E01",
                        size: {x: 50, y: 46},
                        zIndex: 4,
                        callback: function () {
                            localStorage.setItem("TankGameCurrentSelect", this.harder);
                            e.kill()
                        }
                    });
                    e.btns.push(k)
                })(f.label, g)
            }
            var e = this;
            this.closeBtn = ig.game.spawnEntity(GameButton, this.pos.x + this.size.x - this.closeBtnImg.width / 2, this.pos.y - this.closeBtnImg.height / 2, {
                bg: this.closeBtnImg,
                size: {x: this.closeBtnImg.width, y: this.closeBtnImg.height},
                zIndex: 4,
                callback: function () {
                    e.kill()
                }
            });
            this.layerBtn = ig.game.spawnEntity(GameButton, 0, 0, {
                size: {x: ig.system.width, y: ig.system.height},
                zIndex: 4
            })
        },
        update: function () {
            this.parent()
        },
        draw: function () {
            this.parent();
            var a = ig.system.context;
            a.save();
            a.fillStyle = "rgba(0,0,0, 0.5)";
            a.fillRect(0, 0, ig.system.width, ig.system.height);
            a.restore();
            this.bg.draw(this.pos.x, this.pos.y)
        },
        kill: function () {
            this.layerBtn.kill();
            this.closeBtn.kill();
            for (var a = this.btns.length - 1; a >= 0; a--) {
                this.btns[a].kill()
            }
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.gui.loadingpanel").requires("impact.image", "impact.entity", "game.gui.button", "impact.font").defines(function () {
    window.Loadingpanel = ig.Entity.extend({
        text: "", zIndex: 10, init: function (a, c, b) {
            this.parent(a, c, b);
            this.text = b.text || "";
            this.pos = {x: (ig.system.width) / 2 + 30, y: (ig.system.height) / 2};
            this.layerBtn = ig.game.spawnEntity(GameButton, 0, 0, {
                size: {x: ig.system.width, y: ig.system.height},
                zIndex: 11
            });
            this.loadingColors = ["rgba(19, 96, 133, 1)", "rgba(19, 96, 133, 0.85)", "rgba(19, 96, 133, 0.7)", "rgba(19, 96, 133, 0.55)", "rgba(19, 96, 133, 0.4)", "rgba(19, 96, 133, 0.25)", "rgba(19, 96, 133, 0.1)", "rgba(19, 96, 133, 0)"];
            this.loadingColorIndex = 0;
            this.loadingTime = ig.system.clock.last
        }, update: function () {
            this.parent()
        }, draw: function () {
            this.parent();
            var a = ig.system.context;
            a.save();
            a.fillStyle = "rgba(0,0,0, 0.4)";
            a.fillRect(0, 0, ig.system.width, ig.system.height);
            a.restore();
            if (this.text) {
                ig.system.context.fillStyle = "#999";
                ig.system.context.textAlign = "center";
                ig.system.context.font = 'normal 32px "Microsoft YaHei"';
                var b = ig.system.context.measureText(this.text);
                var c = b.width;
                var a = ig.system.context;
                a.save();
                a.beginPath();
                a.fillStyle = "rgba(255, 255 ,255, 0.1)";
                a.strokeStyle = "rgba(0,0,0,0.3)";
                a.moveTo(this.pos.x - (c / 2) - 60, this.pos.y - 42);
                a.lineTo(this.pos.x + c / 2 + 23, this.pos.y - 42);
                a.lineTo(this.pos.x + c / 2 + 23, this.pos.y + 20);
                a.lineTo(this.pos.x - c / 2 - 60, this.pos.y + 20);
                a.fill();
                a.stroke();
                a.closePath();
                a.restore();
                this.drawLoading(c);
                ig.system.context.fillText(this.text, this.pos.x, this.pos.y)
            }
        }, drawLoading: function (c) {
            var b = ig.system.clock.last;
            if (b - this.loadingTime > 0.1) {
                this.loadingTime = b;
                this.loadingColorIndex++;
                if (this.loadingColorIndex >= this.loadingColors.length) {
                    this.loadingColorIndex = 0
                }
            }
            var d = this.loadingColors.length;
            var a = this.pos.x - (c) / 2 - 42;
            var g = this.pos.y - 25;
            var e = 6, f = e;
            this._drawBlock(a, g + f * 2, e, f, this.loadingColors[(this.loadingColorIndex + 7) % d]);
            this._drawBlock(a + e, g + f, e, f, this.loadingColors[(this.loadingColorIndex + 6) % d]);
            this._drawBlock(a + e * 2, g, e, f, this.loadingColors[(this.loadingColorIndex + 5) % d]);
            this._drawBlock(a + e * 3, g + f, e, f, this.loadingColors[(this.loadingColorIndex + 4) % d]);
            this._drawBlock(a + e * 4, g + f * 2, e, f, this.loadingColors[(this.loadingColorIndex + 3) % d]);
            this._drawBlock(a + e * 3, g + f * 3, e, f, this.loadingColors[(this.loadingColorIndex + 2) % d]);
            this._drawBlock(a + e * 2, g + f * 4, e, f, this.loadingColors[(this.loadingColorIndex + 1) % d]);
            this._drawBlock(a + e, g + f * 3, e, f, this.loadingColors[this.loadingColorIndex])
        }, _drawBlock: function (b, f, e, a, d) {
            var c = ig.system.context;
            c.save();
            c.fillStyle = d;
            c.lineWidth = 0;
            c.beginPath();
            c.moveTo(b, f);
            c.lineTo(b + e, f);
            c.lineTo(b + e, f + a);
            c.lineTo(b, f + a);
            c.fill();
            c.restore()
        }, kill: function () {
            this.layerBtn.kill();
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.gui.sharepanel").requires("impact.image", "impact.entity", "game.gui.button").defines(function () {
    window.Sharepanel = ig.Entity.extend({
        image: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/sharetip.png"),
        zIndex: 10,
        init: function (a, d, b) {
            this.parent(a, d, b);
            this.pos = {x: 0, y: 0};
            var c = this;
            this.closeBtn = ig.game.spawnEntity(GameButton, 0, 0, {
                size: {x: ig.system.width, y: ig.system.height},
                zIndex: 11,
                callback: function () {
                    c.kill()
                }
            })
        },
        update: function () {
            this.parent()
        },
        draw: function () {
            this.parent();
            var a = ig.system.context;
            a.save();
            a.fillStyle = "rgba(0,0,0, 0.5)";
            a.fillRect(0, 0, ig.system.width, ig.system.height);
            a.restore();
            this.image.draw(this.pos.x, this.pos.y)
        },
        kill: function () {
            this.closeBtn.kill();
            this.parent()
        }
    })
});
ig.baked = true;
ig.module("game.entities.player").requires("impact.image", "impact.entity").defines(function () {
    window.playerShell = ig.Entity.extend({
        image: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/shell.png"),
        width: 14,
        height: 24,
        size: {x: 14, y: 24},
        tile: 0,
        vel: {x: 0, y: -800},
        maxVel: {x: 0, y: 800},
        type: ig.Entity.TYPE.A,
        checkAgainst: ig.Entity.TYPE.B,
        init: function (a, c, b) {
            this.parent(a, c, b);
            this.tile = 0;
            this.myPos = {x: this.pos.x - ig.game._rscreen.x, y: this.pos.y - ig.game._rscreen.y}
        },
        update: function () {
            this.parent();
            var b = ig.game._rscreen.x;
            var a = ig.game._rscreen.y;
            this.myPos = {x: this.pos.x - b, y: this.pos.y - a};
            if (this.pos.y - a < 0) {
                this.kill()
            }
        },
        draw: function () {
            this.parent();
            this.image.drawTile(this.myPos.x, this.myPos.y, this.tile, this.width, this.height)
        },
        check: function (a) {
            if (a instanceof EntityEnemy) {
                a.shoted();
                this.kill()
            }
        }
    });
    window.EntityPlayer = ig.Entity.extend({
        animSheet: new ig.AnimationSheet("/IMSPActivities/resources/activities/pointgames/media/tank.png", 92, 92),
        width: 92,
        height: 92,
        width: 92,
        height: 92,
        size: {x: 87, y: 74},
        offset: {x: 2, y: 16},
        vel: {x: 0, y: -TankGame.gameInfo.runSpeed},
        maxVel: {x: 0, y: TankGame.gameInfo.runSpeed},
        type: ig.Entity.TYPE.B,
        checkAgainst: ig.Entity.TYPE.A,
        action: "playControl",
        init: function (a, d, b) {
            this.parent(a, d, b);
            this.fireTime = ig.system.clock.last;
            this.isHealth = true;
            this.old = {x: 0, y: 0};
            this.controlEnabled = false;
            this.addAnim("run", 0.1, [9, 10], false);
            this.addAnim("die", 0.1, [11], false);
            this.currentAnim = this.anims.run;
            this.sideDistance = 138;
            var c = this;
            this.touchBtn = ig.game.spawnEntity(GameButton, 0, 46, {
                size: {
                    x: ig.system.width,
                    y: ig.system.height - 46
                }, pressedCallback: function () {
                    if (!c.controlEnabled) {
                        c.old = {x: ig.input.mouse.x, y: ig.input.mouse.y};
                        c.controlEnabled = true
                    }
                }, releasedCallback: function () {
                    c.controlEnabled = false;
                    c.old = {x: 0, y: 0}
                }
            });
            this.shootTime = 0.5
        },
        update: function () {
            this.parent();
            var c = ig.system;
            if (!this.isHealth) {
                if (ig.game.gameStatus == TankGame.STATUS.OVER) {
                    return
                }
                var e = ig.game.getEntitiesByType(playerShell);
                if (!e.length) {
                    TankGame.gameInfo.gameOver(c.clock.last);
                    this.vel.y = 0;
                    ig.game.gameOver()
                }
                return
            }
            var d = c.clock.last;
            if (d - this.fireTime > this.shootTime) {
                this.fireTime = d;
                var a = this.pos.x + this.width / 2 - 7;
                var f = this.pos.y - 12;
                ig.game.spawnEntity(playerShell, a, f);
                ig.game.sortEntities()
            }
            if (this.controlEnabled) {
                var b = ig.input.mouse;
                this.pos.x += b.x - this.old.x;
                this.old = {x: b.x, y: b.y};
                if (this.pos.x < this.sideDistance) {
                    this.pos.x = this.sideDistance
                } else {
                    if (this.pos.x + this.width > c.width - this.sideDistance) {
                        this.pos.x = c.width - this.sideDistance - this.width
                    }
                }
            }
        },
        destroy: function () {
            if (!this.isHealth) {
                return
            }
            this.isHealth = false;
            this.destroyTime = ig.system.clock.last;
            this.currentAnim = this.anims.die
        }
    })
});
ig.baked = true;
ig.module("game.levels.level").requires("impact.image", "game.entities.player", "game.entities.bomb").defines(function () {
    LevelLevel = {
        "entities": [{"type": "EntityBomb", "x": 260, "y": -100}],
        "layer": [{
            "name": "bg",
            "width": 14,
            "height": 2,
            "linkWithCollision": false,
            "visible": 1,
            "tilesetName": "/IMSPActivities/resources/activities/pointgames/media/terrain.png",
            "repeat": true,
            "preRender": false,
            "distance": "1",
            "tilesize": 46,
            "foreground": false,
            "data": [[4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5], [5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4]]
        }, {
            "name": "terrain",
            "width": 14,
            "height": 22,
            "linkWithCollision": false,
            "visible": 1,
            "tilesetName": "/IMSPActivities/resources/activities/pointgames/media/terrain.png",
            "repeat": true,
            "preRender": false,
            "distance": "1",
            "tilesize": 46,
            "foreground": false,
            "data": [[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0], [1, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2], [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 2], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3], [2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0], [2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1], [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3], [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1], [3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2]]
        }]
    };
    LevelLevelResources = [new ig.Image("/IMSPActivities/resources/activities/pointgames/media/terrain.png"), new ig.Image("/IMSPActivities/resources/activities/pointgames/media/terrain.png")]
});
ig.baked = true;
ig.module("game.main").requires("impact.game", "impact.font", "impact.image", "impact.entity", "impact.sound", "game.gui.button", "game.gui.topbar", "game.gui.resultpanel", "game.gui.recordpanel", "game.entities.terrain", "game.entities.enemy", "game.entities.bomb", "game.gui.loadergame", "game.gui.selectpanel", "game.gui.loadingpanel", "game.gui.sharepanel", "game.levels.level").defines(function () {
    TankGame.currentDifficulty = function () {
        var a = localStorage.getItem("TankGameCurrentSelect");
        if (!a || !TankGame.LevelData[a]) {
            a = TankGame.config.defaultDifficulty
        }
        return TankGame.LevelData[a]
    };
    TankGame.ajax = function (b) {
        var a = new XMLHttpRequest();
        a.open(b.type || "get", b.url, true);
        a.setRequestHeader("Accept", "application/json");
        a.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var e = null;
        if (b.data) {
            var c = b.data;
            if (typeof c == "string") {
                e = c
            } else {
                if (typeof c == "object") {
                    e = [];
                    for (var d in c) {
                        e.push(d + "=" + c[d])
                    }
                    e = e.join("&")
                }
            }
        }
        b.loadingTip = b.loadingTip || "正在连接";
        TankGame.loadingTip(b.loadingTip, 3);
        a.send(e);
        a.onreadystatechange = function () {
            clearTimeout(TankGame.loadingTipTimer);
            if (ig.game) {
                var g = ig.game.getEntitiesByType(Loadingpanel);
                for (var h = 0, f = g.length;
                     h < f; h++) {
                    g[h].kill()
                }
            }
            if (a.readyState == 4) {
                if (a.status == 200) {
                    var j = JSON.parse(a.responseText);
                    if (b.success && typeof b.success == "function") {
                        b.success.call(window, j)
                    }
                } else {
                    TankGame.alert("连接服务失败，请重试！")
                }
            }
        }
    };
    TankGame.dealReturnCode = function (b) {
        switch (b.returnCode) {
            case"1000":
                TankGame.alert("请从微信或支付宝进入游戏！");
                break;
            case"1001":
                TankGame.alert("亲，玩游戏请先绑定哦！");
                break;
            case"2000":
                TankGame.alert("亲，服务器很忙，请稍后进入游戏！");
                break;
            case"3001":
                TankGame.alert("无效的签名，请重新进入游戏！");
                break;
            case"3004":
                TankGame.alert("亲，扣除（或增加）积分失败，请稍后重试哦！");
                break;
            case"3005":
                TankGame.alert("打了这么多坦克很累吧？休息一下，明天再来吧！");
                break;
            case"3006":
                clearInterval(codeDoucCount);
                var a = document.querySelector(".codeBtn");
                a.innerHTML = "验证";
                a.className = "codeBtn";
                document.querySelector("#ip").value = "";
                document.querySelector("#code").value = "";
                document.querySelector(".J_ipError").innerHTML = "&nbsp;";
                document.querySelector(".J_codeError").innerHTML = "&nbsp;";
                document.querySelector(".pop").style.display = "block";
                document.querySelector(".scoreNumber").innerHTML = TankGame.gameInfo.totalScore;
                break;
            case"3007":
                TankGame.alert("亲，请文明、健康地进行游戏哦！");
                break;
            case"3008":
                TankGame.alert("亲，请文明、健康地进行游戏哦！");
                break;
            case"1211":
                TankGame.alert("验证码已过期啦，请重新获取验证号！");
                break;
            case"1203":
                TankGame.alert("亲，您的积分不足，还是再赚些积分再来吧~");
                break;
            case"3009":
                TankGame.alert("我们的风险侦测系统发现您的信用卡账户通过非法的网络手段获取积分，为确保账户安全，您暂时无法参与本游戏。");
                break;
            case"1204":
                TankGame.alert("打了这么多坦克很累吧？休息一下，明天再来吧！");
                break;
            case"1210":
                TankGame.alert("验证失败，请重新获取验证码！");
                break;
            default:
                TankGame.alert("亲，服务器很忙，请稍后进入游戏！")
        }
    };
    TankGame.loadingTipTimer = null;
    TankGame.loadingTip = function (b, a) {
        clearTimeout(TankGame.loadingTipTimer);
        if (!ig.game) {
            return
        }
        if (typeof a == "number" && a > 0) {
            TankGame.loadingTipTimer = setTimeout(function () {
                ig.game.spawnEntity(Loadingpanel, 0, 0, {text: b});
                ig.game.sortEntities()
            }, a * 1000)
        } else {
            ig.game.spawnEntity(Loadingpanel, 0, 0, {text: b});
            ig.game.sortEntities()
        }
    };
    TankGame.addCheckCertifyEvent = function () {
        var g = document.querySelector(".subBtn");
        var c = document.querySelector(".codeBtn");
        var e = document.querySelector(".closeBtn");
        var f = document.querySelector(".closeBtn1");
        var a = document.querySelector(".checkBtn");
        var d = false;
        var b = false;
        g.addEventListener("touchstart", function () {
            if (d) {
                return
            }
            var l = document.querySelector("#ip").value, j = document.querySelector("#code").value;
            var i = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
            if (l == "") {
                document.querySelector(".J_ipError").innerHTML = "请填写身份证号";
                return
            } else {
                if (i.test(l) === false) {
                    document.querySelector(".J_ipError").innerHTML = "请正确填写身份证号";
                    return
                } else {
                    document.querySelector(".J_ipError").innerHTML = "&nbsp;"
                }
            }
            if (j == "") {
                document.querySelector(".J_codeError").innerHTML = "请填写手机验证码";
                return
            } else {
                document.querySelector(".J_codeError").innerHTML = "&nbsp;"
            }
            var h = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDR4EadkZgSBmwo4TVN9xyE89+MPe4O4g0mo8Md+R6j2R/sJrqTXWkWjl21Zo6Fe9qOcYakP0xD187R8b+f66a2vVbF15c7/ZR65I6lrY8IAHH9CbjBgAFfcGhs38O7VJSFf0EaA2YC43vvSb9yVmxxAVLD+ZqLRKiPX8MJ+U0gJQIDAQAB";
            var k = new JSEncrypt();
            k.setPublicKey(h);
            l = k.encrypt(l);
            d = true;
            TankGame.ajax({
                type: "post",
                url: "/IMSPActivities/pointgames/confirmIdentity",
                data: TankGame.params.getUrlParams({identityCard: l, verificationCode: j}),
                success: function (m) {
                    if (m.returnCode == "990") {
                        document.querySelector(".J_codeError").innerHTML = "身份证不合法！";
                        return false
                    }
                    d = false;
                    if (m.returnCode == "0000") {
                        document.querySelector(".pop").style.display = "none";
                        TankGame.difficulty = parseInt(m.difficulty);
                        TankGame.gameInfo.serinalNo = m.serinalno || 0;
                        TankGame.gameInfo.setMaxScore(m.maxScore);
                        TankGame.gameInfo.setShootNumber(m.maxScore - 5);
                        ig.system.setGame(TankGame.Play)
                    } else {
                        document.querySelector(".J_codeError").innerHTML = "验证码错误！"
                    }
                }
            })
        }, false);
        c.addEventListener("touchstart", function () {
            if (c.className.indexOf("downCount") > -1) {
                return
            }
            if (b) {
                return
            }
            b = true;
            TankGame.ajax({
                type: "post",
                url: "/IMSPActivities/pointgames/queryMobileVerificationCode",
                data: TankGame.params.getUrlParams(),
                success: function (j) {
                    b = false;
                    if (j.returnCode == "0000") {
                        var i = document.querySelector(".codeBtn");
                        i.className += " downCount";
                        var k = 60;
                        codeDoucCount = setInterval(function () {
                            k--;
                            i.innerHTML = k + "秒";
                            if (k <= 0) {
                                clearInterval(codeDoucCount);
                                i.innerHTML = "验证";
                                i.className = "codeBtn"
                            }
                        }, 1000)
                    } else {
                        var h = document.querySelector(".J_codeError");
                        if (j.returnCode == "1001") {
                            h.innerHTML = "请先绑定哦！"
                        } else {
                            h.innerHTML = "获取验证码失败！"
                        }
                    }
                }
            })
        }, false);
        e.addEventListener("touchstart", function () {
            document.querySelector(".pop").style.display = "none"
        }, false);
        a.addEventListener("touchstart", function () {
            document.querySelector(".tipPop").style.display = "none"
        }, false);
        f.addEventListener("touchstart", function () {
            document.querySelector(".tipPop").style.display = "none"
        }, false)
    };
    TankGame.difficulty = 0;
    TankGame.addCheckCertifyEvent();
    TankGame.alert = function (a) {
        document.querySelector(".J_tipMsg").innerHTML = a;
        document.querySelector(".tipPop").style.display = "block"
    };
    TankGame.loadingUserScore = false;
    TankGame.loadedUserScore = false;
    TankGame.loadingUserScoreNum = 1;
    TankGame.loadUserScore = function () {
        if (TankGame.notHasOpenId || !TankGame.isGameTime) {
            return
        }
        TankGame.loadingUserScore = true;
        TankGame.ajax({
            type: "post",
            url: "/IMSPActivities/pointgames/queryPoints",
            data: TankGame.params.getUrlParams(),
            success: function (a) {
                if (a.returnCode == "0000") {
                    TankGame.loadingUserScore = false;
                    TankGame.bindStatus = a.bindStatus;
                    TankGame.loadedUserScore = true;
                    TankGame.gameInfo.setTotalScore(parseInt(a.currentPoints));
                    if (ig.game) {
                        ig.game.getEntitiesByType(GameTopbar)[0].updateScore(true)
                    }
                } else {
                    if (a.returnCode == "2000") {
                        if (TankGame.loadingUserScoreNum > 6) {
                            TankGame.alert("亲，获取您的积分信息失败啦，请重新进入游戏吧！")
                        } else {
                            TankGame.loadingUserScoreNum++;
                            TankGame.loadUserScore()
                        }
                    } else {
                        TankGame.dealReturnCode(a)
                    }
                }
            }
        })
    };
    if (!TankGame.params.openId || !TankGame.params.gameNo || !TankGame.params.from) {
        TankGame.notHasOpenId = true
    } else {
        TankGame.loadUserScore()
    }
    TankGame.payScoreing = false;
    TankGame.payScoreToPlay = function () {
        if (TankGame.payScoreing) {
            return
        }
        TankGame.payScoreing = true;
        if (TankGame.bindStatus != 3) {
            if (TankGame.params.from == "161") {
                window.location.href = "http://market.cmbchina.com/ccard/weixin/news/zjy/wkf_5.html";
                return false
            } else {
                window.location.href = "http://xyk.cmbchina.com/Latte/tips/zfb/20150706";
                return false
            }
        }
        TankGame.ajax({
            type: "post",
            url: "/IMSPActivities/pointgames/decreasePoints",
            data: TankGame.params.getUrlParams(),
            success: function (a) {
                TankGame.payScoreing = false;
                if (a.returnCode == "0000") {
                    TankGame.difficulty = parseInt(a.difficulty);
                    TankGame.gameInfo.setMaxScore(a.maxScore);
                    TankGame.gameInfo.setShootNumber(a.maxScore - 5);
                    TankGame.gameInfo.serinalNo = a.serinalno || 0;
                    ig.system.setGame(TankGame.Play)
                } else {
                    if (a.returnCode == "1206") {
                        TankGame.isGameTime = false;
                        if (ig.game instanceof TankGame.Start) {
                            ig.game.noInGameTime()
                        } else {
                            ig.system.setGame(TankGame.Start)
                        }
                    } else {
                        TankGame.dealReturnCode(a)
                    }
                }
            }
        })
    };
    TankGame.Start = ig.Game.extend({
        title: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/title.png"),
        logo: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/logo.png"),
        startImg: new ig.Image("/IMSPActivities/resources/activities/pointgames/media/start_btn.png"),
        clearColor: "#282922",
        init: function () {
            if (TankGame.notHasOpenId || !TankGame.isGameTime) {
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 2, ig.system.height * 0.3 + this.title.height + 110, {tile: 1});
                this.spawnEntity(EntityTerrain, 46, ig.system.height - 46 * 5);
                this.spawnEntity(EntityTerrain, ig.system.width - 46, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, ig.system.width - 46, ig.system.height - 46 * 2);
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 2, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 3, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, 0, ig.system.height - 46, {tile: 1});
                this.spawnEntity(EntityTerrain, 0, ig.system.height - 46 * 2, {tile: 1});
                this.spawnEntity(EntityTerrain, 46, ig.system.height - 46, {tile: 1});
                if (TankGame.notHasOpenId) {
                    this.tipTexts = ["系统不给力，此路不通哦", "请坦克er从招商银行信用卡官方服务号", "进入游戏爽玩吧"];
                    this.fontSize = 32;
                    document.querySelector(".pcode").style.display = "block"
                } else {
                    this.tipTexts = ["休息，休息一会", "9分坦克战开放时间为", "周一~周五 9:00~18:00"];
                    this.fontSize = 36
                }
            } else {
                this.gameButtonManager = new GameButtonManager(true);
                this.spawnEntity(GameButton, (ig.system.width - this.startImg.width) / 2, ig.system.height - this.startImg.height - 46 * 2, {
                    bg: this.startImg,
                    size: {x: 178, y: 152},
                    name: "start",
                    callback: function () {
                        if (!TankGame.loadedUserScore) {
                            TankGame.alert("正在获取您的信息，请稍后开始游戏！");
                            return
                        }
                        if (TankGame.gameInfo.isPayEnabled()) {
                            TankGame.payScoreToPlay()
                        } else {
                            if (TankGame.bindStatus != 3) {
                                if (TankGame.params.from == "161") {
                                    window.location.href = "http://market.cmbchina.com/ccard/weixin/news/zjy/wkf_5.html";
                                    return false
                                } else {
                                    window.location.href = "http://xyk.cmbchina.com/Latte/tips/zfb/20150706";
                                    return false
                                }
                            }
                            TankGame.alert(TankGame.gameInfo.canNotPayTip)
                        }
                    }
                });
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 2, ig.system.height * 0.54);
                this.spawnEntity(EntityTerrain, 46, ig.system.height - 46 * 5);
                this.spawnEntity(EntityTerrain, ig.system.width - 46, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, ig.system.width - 46, ig.system.height - 46 * 2);
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 2, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, ig.system.width - 46 * 3, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, 0, ig.system.height - 46);
                this.spawnEntity(EntityTerrain, 0, ig.system.height - 46 * 2);
                this.spawnEntity(EntityTerrain, 46, ig.system.height - 46);
                this.spawnEntity(GameTopbar, 0, 0)
            }
        },
        update: function () {
            if (!TankGame.notHasOpenId && TankGame.isGameTime) {
                this.gameButtonManager.update()
            }
            this.parent()
        },
        draw: function () {
            ig.system.clear(this.clearColor);
            this.title.draw((ig.system.width - this.title.width) / 2, ig.system.height * 0.2);
            this.logo.draw((ig.system.width - this.logo.width) / 2, ig.system.height * 0.14);
            if (TankGame.notHasOpenId || !TankGame.isGameTime) {
                var d = ig.system.context;
                d.save();
                d.fillStyle = "#fff";
                d.textAlign = "center";
                d.font = "normal " + this.fontSize + 'px "Microsoft YaHei"';
                var a = ig.system.height * 0.3 + this.title.height;
                for (var e = 0, c = this.tipTexts.length; e < c; e++) {
                    var b = ig.system.width / 2;
                    ig.system.context.fillText(this.tipTexts[e], b, a + e * (this.fontSize * 1.5))
                }
                d.restore()
            }
            this.drawEntities()
        },
        noInGameTime: function () {
            for (var b = 0, a = this.entities.length; b < a; b++) {
                this.entities[b].kill()
            }
            this.init()
        }
    });
    TankGame.Play = ig.Game.extend({
        init: function () {
            this.restart()
        }, loadLevel: function (b) {
            this.harder = TankGame.currentDifficulty();
            this.gameButtonManager = new GameButtonManager(true);
            if (!TankGame.gameInfo.isPayEnabled()) {
                TankGame.alert(TankGame.gameInfo.canNotPayTip);
                return
            }
            this.parent(b);
            this._rscreen = {x: 0, y: 0};
            this.spawnEntity(EntityEnemy, 138, 92, {arriveY: 92});
            this.spawnEntity(EntityEnemy, 276, 138, {arriveY: 138});
            this.spawnEntity(EntityEnemy, 414, 92, {arriveY: 92});
            this.spawnEntity(EntityPlayer, 276, ig.system.height - 138, {arriveY: 92});
            var a = this.spawnEntity(GameTopbar, 0, 0);
            this.gameStartTime = ig.system.clock.last;
            this.bombtime = ig.system.clock.last;
            this.gameStatus = TankGame.STATUS.START;
            TankGame.gameInfo.reset();
            TankGame.gameInfo.payScored();
            a.updateScore();
            this.setBombConfig()
        }, setBombConfig: function () {
            var a = TankGame.difficulty;
            this.minShootBombTime = 2.5 - a / 15;
            this.maxShootBombTime = 4 - a / 10;
            this.diffShootBombTime = this.maxShootBombTime - this.minShootBombTime;
            this.nextShootBombTime = Math.min(this.minShootBombTime + Math.random() * this.diffShootBombTime, this.maxShootBombTime);
            this.bombNumber = 2 + a / 5;
            this.difficulty = a
        }, restart: function () {
            ig.game.loadLevel(LevelLevel)
        }, update: function () {
            this.gameButtonManager.update();
            this.parent();
            if (this.difficulty != TankGame.difficulty) {
                this.setBombConfig()
            }
            var g = ig.game.screen;
            var a = ig.system;
            var e = this.getEntitiesByType(EntityPlayer)[0];
            g.y = e.pos.y - a.height + e.height + 46;
            var f = this.nextShootBombTime;
            var b = this.bombNumber;
            if (TankGame.gameInfo.number >= TankGame.gameInfo.shootNumber) {
                f = 0.3;
                b *= 2
            }
            if (this.gameStatus == TankGame.STATUS.OVER) {
                return
            }
            var d = a.clock.last;
            if (d - this.bombtime >= f) {
                this.bombtime = d;
                this.addBomb(e.pos.x, e.pos.y - a.height);
                var c = Math.round(Math.random() * (b - 1));
                while (c > 0) {
                    c--;
                    this.addBomb(Math.random() * a.width, e.pos.y - a.height)
                }
            }
        }, draw: function () {
            this.parent()
        }, addEnemy: function (a, e, d) {
            var c = Math.floor(Math.random() * 3);
            var b = this.spawnEntity(EntityEnemy, a, e, {vel: {x: 0, y: -10}, arriveY: d, tankColor: c})
        }, addBomb: function (a, b) {
            this.spawnEntity(EntityBomb, a, b);
            ig.game.sortEntities()
        }, gameOver: function () {
            this.gameStatus = TankGame.STATUS.OVER;
            TankGame.gameInfo.gameOver(ig.system.clock.last);
            var y = hex_md5(TankGame.gameInfo.number + "cmbchina" + TankGame.gameInfo.serinalNo + "CMBCHINA");
            TankGame.ajax({
                type: "post",
                url: "/IMSPActivities/pointgames/increasePoints",
                data: TankGame.params.getUrlParams({
                    score: TankGame.gameInfo.number,
                    serinalno: TankGame.gameInfo.serinalNo,
                    sign: y
                }),
                success: function (b) {
                    totalnum = TankGame.gameInfo.number;
                    if (b.returnCode == "0000") {
                        TankGame.gameInfo.setPercent(b.percentage.replace("%", "I"));
                        TankGame.gameInfo.setTotal(b.totalScore);
                        TankGame.gameInfo.addScore(TankGame.gameInfo.number);
                        var a = ig.game.getEntitiesByType(GameTopbar)[0];
                        a.updateScore();
                        ig.game.spawnEntity(Resultpanel, (ig.system.width - 537) / 2, (ig.system.height - 338) / 2);
                        ig.game.sortEntitiesDeferred()
                    } else {
                        if (b.returnCode == "1206") {
                            TankGame.isGameTime = false;
                            ig.system.setGame(TankGame.Start)
                        } else {
                            if (b.returnCode == "3004") {
                                TankGame.alert("亲，您的积分未成功获得，请重新游戏哦！")
                            } else {
                                TankGame.dealReturnCode(b)
                            }
                        }
                    }
                }
            })
        }
    });
    TankGame.STATUS = {START: 1, OVER: 2};
    TankGame.getWinSize = function () {
        var a, d;
        if (window.innerWidth) {
            a = parseInt(window.innerWidth) > parseInt(document.documentElement.clientWidth) ? parseInt(document.documentElement.clientWidth) : parseInt(window.innerWidth);
            d = parseInt(window.innerHeight) > parseInt(document.documentElement.clientHeight) ? parseInt(document.documentElement.clientHeight) : parseInt(window.innerHeight)
        } else {
            a = parseInt(document.body.clientWidth) > parseInt(document.documentElement.clientWidth) ? parseInt(document.documentElement.clientWidth) : parseInt(document.body.clientWidth);
            d = parseInt(document.body.clientHeight) > parseInt(document.documentElement.clientHeight) ? parseInt(document.documentElement.clientHeight) : parseInt(document.body.clientHeight)
        }
        return [a, d]
    };
    TankGame.loadGame = function () {
        ig.Sound.channels = 2;
        ig.System.drawMode = ig.System.DRAW.SUBPIXEL;
        var e = 644;
        var a = 1012;
        var h = 1;
        if (ig.ua.mobile) {
            ig.Sound.use = [ig.Sound.FORMAT.MP3];
            ig.Sound.enabled = true;
            var d = TankGame.getWinSize();
            var b = d[0] * ig.ua.pixelRatio;
            var g = d[1];
            var f = g * ig.ua.pixelRatio;
            h = e / b;
            a = f * h;
            ig.internalScale = h * ig.ua.pixelRatio;
            var c = ig.$("#canvas");
            c.style.width = Math.floor(d[0]) + "px";
            c.style.height = Math.floor(d[1]) + "px"
        } else {
        }
        ig.main("#canvas", TankGame.Start, 60, e, a, 1, LoaderGame)
    };
    TankGame.loadGame()
});