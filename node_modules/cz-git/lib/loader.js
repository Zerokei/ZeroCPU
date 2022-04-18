"use strict";
/**
 * @description: generate commitizen config option(generateOptions) | generate commitizen questions(generateQuestions)
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 */
var _a;
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateQuestions = exports.generateOptions = void 0;
// @ts-ignore
var loader_1 = require("@cz-git/loader");
var share_1 = require("./share");
var until_1 = require("./until");
/**
 * @description: Compatibility support for cz-conventional-changelog
 */
var _b = process.env, CZ_SCOPE = _b.CZ_SCOPE, CZ_SUBJECT = _b.CZ_SUBJECT, CZ_BODY = _b.CZ_BODY, CZ_ISSUES = _b.CZ_ISSUES, CZ_MAN_HEADER_LENGTH = _b.CZ_MAN_HEADER_LENGTH, CZ_MAN_SUBJECT_LENGTH = _b.CZ_MAN_SUBJECT_LENGTH, CZ_MIN_SUBJECT_LENGTH = _b.CZ_MIN_SUBJECT_LENGTH;
var pkgConfig = (_a = (0, loader_1.commitizenConfigLoader)()) !== null && _a !== void 0 ? _a : {};
/* eslint-disable prettier/prettier */
/* prettier-ignore */
var generateOptions = function (clConfig) {
    var _a, _b, _c, _d, _e, _f, _g, _h, _j, _k, _l, _m, _o, _p, _q, _r, _s, _t, _u, _v, _w, _x, _y, _z, _0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24, _25, _26, _27, _28, _29, _30, _31, _32, _33;
    var clPromptConfig = (_a = clConfig.prompt) !== null && _a !== void 0 ? _a : {};
    clPromptConfig = (0, until_1.getValueByCallBack)(clPromptConfig, ["defaultScope", "defaultSubject", "defaultBody", "defaultFooterPrefix", "defaultIssues"]);
    return {
        messages: (_c = (_b = pkgConfig.messages) !== null && _b !== void 0 ? _b : clPromptConfig.messages) !== null && _c !== void 0 ? _c : share_1.defaultConfig.messages,
        types: (_e = (_d = pkgConfig.types) !== null && _d !== void 0 ? _d : clPromptConfig.types) !== null && _e !== void 0 ? _e : share_1.defaultConfig.types,
        typesAppend: (_g = (_f = pkgConfig.typesAppend) !== null && _f !== void 0 ? _f : clPromptConfig.typesAppend) !== null && _g !== void 0 ? _g : share_1.defaultConfig.typesAppend,
        useEmoji: (_j = (_h = pkgConfig.useEmoji) !== null && _h !== void 0 ? _h : clPromptConfig.useEmoji) !== null && _j !== void 0 ? _j : share_1.defaultConfig.useEmoji,
        scopes: (_l = (_k = pkgConfig.scopes) !== null && _k !== void 0 ? _k : clPromptConfig.scopes) !== null && _l !== void 0 ? _l : (0, until_1.getEnumList)((_m = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _m === void 0 ? void 0 : _m["scope-enum"]),
        scopeOverrides: (_p = (_o = pkgConfig.scopeOverrides) !== null && _o !== void 0 ? _o : clPromptConfig.scopeOverrides) !== null && _p !== void 0 ? _p : share_1.defaultConfig.scopeOverrides,
        allowCustomScopes: (_r = (_q = pkgConfig.allowCustomScopes) !== null && _q !== void 0 ? _q : clPromptConfig.allowCustomScopes) !== null && _r !== void 0 ? _r : !(0, until_1.enumRuleIsActive)((_s = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _s === void 0 ? void 0 : _s["scope-enum"]),
        allowEmptyScopes: (_u = (_t = pkgConfig.allowEmptyScopes) !== null && _t !== void 0 ? _t : clPromptConfig.allowEmptyScopes) !== null && _u !== void 0 ? _u : share_1.defaultConfig.allowEmptyScopes,
        customScopesAlign: (_w = (_v = pkgConfig.customScopesAlign) !== null && _v !== void 0 ? _v : clPromptConfig.customScopesAlign) !== null && _w !== void 0 ? _w : share_1.defaultConfig.customScopesAlign,
        customScopesAlias: (_y = (_x = pkgConfig.customScopesAlias) !== null && _x !== void 0 ? _x : clPromptConfig.customScopesAlias) !== null && _y !== void 0 ? _y : share_1.defaultConfig.customScopesAlias,
        emptyScopesAlias: (_0 = (_z = pkgConfig.emptyScopesAlias) !== null && _z !== void 0 ? _z : clPromptConfig.emptyScopesAlias) !== null && _0 !== void 0 ? _0 : share_1.defaultConfig.emptyScopesAlias,
        upperCaseSubject: (_2 = (_1 = pkgConfig.upperCaseSubject) !== null && _1 !== void 0 ? _1 : clPromptConfig.upperCaseSubject) !== null && _2 !== void 0 ? _2 : share_1.defaultConfig.upperCaseSubject,
        allowBreakingChanges: (_4 = (_3 = pkgConfig.allowBreakingChanges) !== null && _3 !== void 0 ? _3 : clPromptConfig.allowBreakingChanges) !== null && _4 !== void 0 ? _4 : share_1.defaultConfig.allowBreakingChanges,
        breaklineNumber: (0, until_1.getMaxLength)((_5 = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _5 === void 0 ? void 0 : _5["body-max-line-length"]) === Infinity
            ? (_7 = (_6 = pkgConfig.breaklineNumber) !== null && _6 !== void 0 ? _6 : clPromptConfig.breaklineNumber) !== null && _7 !== void 0 ? _7 : share_1.defaultConfig.breaklineNumber
            : (0, until_1.getMaxLength)((_8 = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _8 === void 0 ? void 0 : _8["body-max-line-length"]),
        breaklineChar: (_10 = (_9 = pkgConfig.breaklineChar) !== null && _9 !== void 0 ? _9 : clPromptConfig.breaklineChar) !== null && _10 !== void 0 ? _10 : share_1.defaultConfig.breaklineChar,
        skipQuestions: (_12 = (_11 = pkgConfig.skipQuestions) !== null && _11 !== void 0 ? _11 : clPromptConfig.skipQuestions) !== null && _12 !== void 0 ? _12 : share_1.defaultConfig.skipQuestions,
        issuePrefixs: (_14 = (_13 = pkgConfig.issuePrefixs) !== null && _13 !== void 0 ? _13 : clPromptConfig.issuePrefixs) !== null && _14 !== void 0 ? _14 : share_1.defaultConfig.issuePrefixs,
        customIssuePrefixsAlign: (_16 = (_15 = pkgConfig.customIssuePrefixsAlign) !== null && _15 !== void 0 ? _15 : clPromptConfig.customIssuePrefixsAlign) !== null && _16 !== void 0 ? _16 : share_1.defaultConfig.customIssuePrefixsAlign,
        emptyIssuePrefixsAlias: (_18 = (_17 = pkgConfig.emptyIssuePrefixsAlias) !== null && _17 !== void 0 ? _17 : clPromptConfig.emptyIssuePrefixsAlias) !== null && _18 !== void 0 ? _18 : share_1.defaultConfig.emptyIssuePrefixsAlias,
        customIssuePrefixsAlias: (_20 = (_19 = pkgConfig.customIssuePrefixsAlias) !== null && _19 !== void 0 ? _19 : clPromptConfig.customIssuePrefixsAlias) !== null && _20 !== void 0 ? _20 : share_1.defaultConfig.customIssuePrefixsAlias,
        confirmColorize: (_22 = (_21 = pkgConfig.confirmColorize) !== null && _21 !== void 0 ? _21 : clPromptConfig.confirmColorize) !== null && _22 !== void 0 ? _22 : share_1.defaultConfig.confirmColorize,
        maxHeaderLength: CZ_MAN_HEADER_LENGTH
            ? parseInt(CZ_MAN_HEADER_LENGTH)
            : (_23 = clPromptConfig.maxHeaderLength) !== null && _23 !== void 0 ? _23 : (0, until_1.getMaxLength)((_24 = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _24 === void 0 ? void 0 : _24["header-max-length"]),
        maxSubjectLength: CZ_MAN_SUBJECT_LENGTH
            ? parseInt(CZ_MAN_SUBJECT_LENGTH)
            : (_25 = clPromptConfig.maxSubjectLength) !== null && _25 !== void 0 ? _25 : (0, until_1.getMaxLength)((_26 = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _26 === void 0 ? void 0 : _26["subject-max-length"]),
        minSubjectLength: CZ_MIN_SUBJECT_LENGTH
            ? parseInt(CZ_MIN_SUBJECT_LENGTH)
            : (_27 = clPromptConfig.minSubjectLength) !== null && _27 !== void 0 ? _27 : (0, until_1.getMinLength)((_28 = clConfig === null || clConfig === void 0 ? void 0 : clConfig.rules) === null || _28 === void 0 ? void 0 : _28["subject-min-length"]),
        defaultScope: (_29 = CZ_SCOPE !== null && CZ_SCOPE !== void 0 ? CZ_SCOPE : clPromptConfig.defaultScope) !== null && _29 !== void 0 ? _29 : share_1.defaultConfig.defaultScope,
        defaultSubject: (_30 = CZ_SUBJECT !== null && CZ_SUBJECT !== void 0 ? CZ_SUBJECT : clPromptConfig.defaultSubject) !== null && _30 !== void 0 ? _30 : share_1.defaultConfig.defaultSubject,
        defaultBody: (_31 = CZ_BODY !== null && CZ_BODY !== void 0 ? CZ_BODY : clPromptConfig.defaultBody) !== null && _31 !== void 0 ? _31 : share_1.defaultConfig.defaultBody,
        defaultFooterPrefix: (_32 = clPromptConfig.defaultFooterPrefix) !== null && _32 !== void 0 ? _32 : share_1.defaultConfig.defaultFooterPrefix,
        defaultIssues: (_33 = CZ_ISSUES !== null && CZ_ISSUES !== void 0 ? CZ_ISSUES : clPromptConfig.defaultIssues) !== null && _33 !== void 0 ? _33 : share_1.defaultConfig.defaultIssues
    };
};
exports.generateOptions = generateOptions;
var generateQuestions = function (options, cz) {
    var _a, _b, _c, _d, _e, _f, _g, _h, _j;
    if (!Array.isArray(options.types) || options.types.length === 0) {
        (0, until_1.log)("err", "Error [types] Option");
        return false;
    }
    return [
        {
            type: "autocomplete",
            name: "type",
            message: (_a = options.messages) === null || _a === void 0 ? void 0 : _a.type,
            source: function (_, input) {
                var _a;
                var typesSource = ((_a = options.types) === null || _a === void 0 ? void 0 : _a.concat(options.typesAppend || [])) || [];
                return typesSource.filter(function (item) { return (input ? item.value.includes(input) : true); }) || true;
            }
        },
        {
            type: "autocomplete",
            name: "scope",
            message: (_b = options.messages) === null || _b === void 0 ? void 0 : _b.scope,
            source: function (answer, input) {
                var scopes = [];
                if (options.scopeOverrides && answer.type && options.scopeOverrides[answer.type]) {
                    scopes = (0, until_1.handleStandardScopes)(options.scopeOverrides[answer.type]);
                }
                else if (Array.isArray(options.scopes)) {
                    scopes = (0, until_1.handleStandardScopes)(options.scopes);
                }
                scopes = (0, until_1.handleCustomTemplate)(scopes, cz, options.customScopesAlign, options.emptyScopesAlias, options.customScopesAlias, options.allowCustomScopes, options.allowEmptyScopes);
                return (scopes === null || scopes === void 0 ? void 0 : scopes.filter(function (item) { var _a; return (input ? (_a = item.name) === null || _a === void 0 ? void 0 : _a.includes(input) : true); })) || true;
            }
        },
        {
            type: "input",
            name: "scope",
            message: (_c = options.messages) === null || _c === void 0 ? void 0 : _c.customScope,
            default: options.defaultScope || undefined,
            when: function (answers) {
                return answers.scope === "___CUSTOM___";
            }
        },
        {
            type: "input",
            name: "subject",
            message: (_d = options.messages) === null || _d === void 0 ? void 0 : _d.subject,
            validate: function (subject, answers) {
                var processedSubject = (0, until_1.getProcessSubject)(subject);
                if (processedSubject.length === 0)
                    return "\u001B[1;31m[ERROR] subject is required\u001B[0m";
                if (!options.minSubjectLength && !options.maxSubjectLength) {
                    (0, until_1.log)("err", "Error [Subject Length] Option");
                    return false;
                }
                var maxSubjectLength = (0, until_1.getMaxSubjectLength)(answers.type, answers.scope, options);
                if (options.minSubjectLength && processedSubject.length < options.minSubjectLength)
                    return "\u001B[1;31m[ERROR]subject length must be greater than or equal to ".concat(options.minSubjectLength, " characters\u001B[0m");
                if (processedSubject.length > maxSubjectLength)
                    return "\u001B[1;31m[ERROR]subject length must be less than or equal to ".concat(maxSubjectLength, " characters\u001B[0m");
                return true;
            },
            transformer: function (subject, answers) {
                var minSubjectLength = options.minSubjectLength;
                var subjectLength = subject.length;
                var maxSubjectLength = (0, until_1.getMaxSubjectLength)(answers.type, answers.scope, options);
                var tooltip;
                if (minSubjectLength !== undefined && subjectLength < minSubjectLength)
                    tooltip = "".concat(minSubjectLength - subjectLength, " more chars needed");
                else if (subjectLength > maxSubjectLength)
                    tooltip = "".concat(subjectLength - maxSubjectLength, " chars over the limit");
                else
                    tooltip = "".concat(maxSubjectLength - subjectLength, " more chars allowed");
                var tooltipColor = minSubjectLength !== undefined &&
                    subjectLength >= minSubjectLength &&
                    subjectLength <= maxSubjectLength
                    ? "\u001B[90m"
                    : "\u001B[31m";
                var subjectColor = minSubjectLength !== undefined &&
                    subjectLength >= minSubjectLength &&
                    subjectLength <= maxSubjectLength
                    ? "\u001B[36m"
                    : "\u001B[31m";
                return "".concat(tooltipColor, "[").concat(tooltip, "]\u001B[0m\n  ").concat(subjectColor).concat(subject, "\u001B[0m");
            },
            filter: function (subject) {
                var upperCaseSubject = options.upperCaseSubject || false;
                return ((upperCaseSubject ? subject.charAt(0).toUpperCase() : subject.charAt(0).toLowerCase()) +
                    subject.slice(1));
            },
            default: options.defaultSubject || undefined
        },
        {
            type: "input",
            name: "body",
            message: (_e = options.messages) === null || _e === void 0 ? void 0 : _e.body,
            default: options.defaultBody || undefined
        },
        {
            type: "input",
            name: "breaking",
            message: (_f = options.messages) === null || _f === void 0 ? void 0 : _f.breaking,
            default: options.defaultBody || undefined,
            when: function (answers) {
                if (options.allowBreakingChanges &&
                    answers.type &&
                    options.allowBreakingChanges.includes(answers.type)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        },
        {
            type: "autocomplete",
            name: "footerPrefix",
            message: (_g = options.messages) === null || _g === void 0 ? void 0 : _g.footerPrefixsSelect,
            source: function (_, input) {
                var issues = (0, until_1.handleCustomTemplate)(options.issuePrefixs, cz, options.customIssuePrefixsAlign, options.emptyIssuePrefixsAlias, options.customIssuePrefixsAlias);
                return (issues === null || issues === void 0 ? void 0 : issues.filter(function (item) { var _a; return (input ? (_a = item.name) === null || _a === void 0 ? void 0 : _a.includes(input) : true); })) || true;
            }
        },
        {
            type: "input",
            name: "footerPrefix",
            message: (_h = options.messages) === null || _h === void 0 ? void 0 : _h.customFooterPrefixs,
            default: options.defaultIssues || undefined,
            when: function (answers) {
                return answers.footerPrefix === "___CUSTOM___";
            }
        },
        {
            type: "input",
            name: "footer",
            default: options.defaultIssues || undefined,
            when: function (answers) {
                return answers.footerPrefix !== false;
            },
            message: (_j = options.messages) === null || _j === void 0 ? void 0 : _j.footer
        },
        {
            type: "expand",
            name: "confirmCommit",
            choices: [
                { key: "y", name: "Yes", value: "yes" },
                { key: "n", name: "Abort commit", value: "no" },
                { key: "e", name: "Edit message", value: "edit" }
            ],
            default: 0,
            message: function (answers) {
                var _a;
                var SEP = options.confirmColorize
                    ? "\u001B[1;90m###--------------------------------------------------------###\u001B[0m"
                    : "###--------------------------------------------------------###";
                console.info("\n".concat(SEP, "\n").concat((0, until_1.buildCommit)(answers, options, options.confirmColorize), "\n").concat(SEP, "\n"));
                return (_a = options.messages) === null || _a === void 0 ? void 0 : _a.confirmCommit;
            }
        }
    ].filter(function (i) { var _a; return !((_a = options.skipQuestions) === null || _a === void 0 ? void 0 : _a.includes(i.name)); });
};
exports.generateQuestions = generateQuestions;
//# sourceMappingURL=loader.js.map