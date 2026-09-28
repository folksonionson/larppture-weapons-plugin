// Offline sandbox build: compiles the plugin with the WASM-ported OpenJDK 21
// javac from @wasm-oj/toolchain-java (npm), against the compile-time stub tree
// in tools/sandbox-stubs (exact mirrors of the Bukkit signatures used).
//
// Usage: node tools/build_plugin.mjs [assetsDir]
//   assetsDir defaults to $JAVAC_WASM_DIR or /home/user/.toolchain/package/assets
//
// Output: dist/classes/**.class  (plugin packages only; stubs are discarded)
import { readFile, writeFile, mkdir } from "node:fs/promises";
import { existsSync, createReadStream } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const HERE = path.dirname(fileURLToPath(import.meta.url));
const REPO = path.resolve(HERE, "..");
const ASSETS = process.argv[2] || process.env.JAVAC_WASM_DIR
    || "/home/user/.toolchain/package/assets";

// ---- load the vendored TeaVM/javac host code (from @wasm-oj/server) --------
const SERVER_STAGE = process.env.JAVA_STAGE_MJS
    || "/home/user/.toolchain/x-woj-server/package/dist/java-stage.mjs";
const stageSrc = await readFile(SERVER_STAGE, "utf8");
const cut = stageSrc.indexOf("//#region src/server/java-stage.mjs");
const vendorSrc = stageSrc.slice(0, cut) + "\nexport { loadJavaGcEngine, compileJavaGc };\n";
const vendorFile = path.join(path.dirname(SERVER_STAGE), "vendor-export.mjs");
if (!existsSync(vendorFile) || (await readFile(vendorFile, "utf8")) !== vendorSrc) {
    await writeFile(vendorFile, vendorSrc);
}
const vendor = await import(vendorFile);

const compilerBytes = new Uint8Array(await readFile(path.join(ASSETS, "java-teavm-0.13.1.compiler.wasm")));
const sdkBytes = new Uint8Array(await readFile(path.join(ASSETS, "java-teavm-0.13.1.compile-classlib.bin")));

// ---- gather sources --------------------------------------------------------
async function walk(dir, out = []) {
    for (const entry of await (await import("node:fs/promises")).readdir(dir, { withFileTypes: true })) {
        const p = path.join(dir, entry.name);
        if (entry.isDirectory()) await walk(p, out);
        else if (entry.name.endsWith(".java")) out.push(p);
    }
    return out;
}
const pluginSrcs = await walk(path.join(REPO, "plugin/src/main/java"));
const stubSrcs = await walk(path.join(REPO, "tools/sandbox-stubs"));

const files = [];
for (const p of [...pluginSrcs, ...stubSrcs]) {
    const rel = path.relative(REPO, p).split(path.sep).join("/");
    files.push({ path: "/" + rel, content: await readFile(p, "utf8") });
}

// ---- compile ---------------------------------------------------------------
const engine = await vendor.loadJavaGcEngine(compilerBytes);
const compiler = engine.createCompiler();
compiler.setSdk(new Int8Array(sdkBytes.buffer, sdkBytes.byteOffset, sdkBytes.byteLength));
const diagnostics = [];
compiler.onDiagnostic((d) => diagnostics.push(d));
for (const file of files) compiler.addSourceFile(file.path, file.content);
const ok = compiler.compile();
const errs = diagnostics.filter((d) => d.severity === "error");
for (const d of diagnostics.slice(0, 80)) {
    console.log(`${d.fileName ?? "?"}:${d.lineNumber ?? 0}: [${d.type}/${d.severity}] ${d.message}`);
}
if (!ok || errs.length) {
    console.error(`COMPILATION FAILED (${errs.length} errors)`);
    process.exit(1);
}

// ---- extract class files ---------------------------------------------------
const outDir = path.join(REPO, "dist/classes");
await mkdir(outDir, { recursive: true });
const names = compiler.listOutputFiles();
let written = 0;
for (const name of names) {
    if (!name.endsWith(".class")) continue;
    if (!name.includes("dev/larppture")) continue; // stubs stay out of the jar
    const bytes = compiler.getOutputFile(name);
    if (!bytes) continue;
    const target = path.join(outDir, name.replace(/^\//, ""));
    await mkdir(path.dirname(target), { recursive: true });
    await writeFile(target, Buffer.from(Uint8Array.from(bytes)));
    written++;
}
console.log(`OK: compiled ${files.length} sources, wrote ${written} class files to dist/classes`);
console.log("outputs:", names.filter((n) => n.includes("dev/larppture")).join(", "));
